package io.github.icepony.alwaysbatterysaver;

import android.app.Activity;

import java.lang.reflect.Field;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook extends XposedHelper implements IXposedHookLoadPackage {
    public static final int REASON_CODE_PLUGGED_IN = 7;
    public static final String REASON_STRING_PLUGGED_IN = "Plugged in";

    private boolean isModuleEnabled;
    private boolean isFakePower;
    private boolean isLockOnPluggedIn;
    private boolean isLockOnPower;
    private boolean isLockAny;
    private boolean isPowered = false;

    private void reloadPreferences(Object thisObject) {
        prefs.reload();
        isModuleEnabled = prefs.getBoolean("enable_module", true);
        isLockOnPluggedIn = prefs.getBoolean("lock_on_plugged_in", true);
        isLockOnPower = prefs.getBoolean("lock_on_power", false);
        isFakePower = prefs.getBoolean("fake_power", false);
        isLockAny = prefs.getBoolean("lock_any", false);


        try {
            isPowered = mIsPoweredField.getBoolean(thisObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> batterySaverStateMachineClass;
    private Field mIsPoweredField;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            log("Handling Android package");

            batterySaverStateMachineClass = findClass("com.android.server.power.batterysaver.BatterySaverStateMachine", lpparam.classLoader);
            if (batterySaverStateMachineClass == null) {
                log("BatterySaverStateMachine class not found.");
                return;
            }
            mIsPoweredField = findField(batterySaverStateMachineClass, "mIsPowered");
            hookIsPowered();
            hookBatterySaverEnabler();
        } else if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            findAndHookMethod(BuildConfig.APPLICATION_ID + ".MainActivity", lpparam.classLoader,
                    "showActivationWarning", Activity.class, XC_MethodReplacement.DO_NOTHING);
        }
    }

    private void hookBatterySaverEnabler() {
        hookAllMethods(batterySaverStateMachineClass, "enableBatterySaverLocked", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                reloadPreferences(param.thisObject);
                if (!isModuleEnabled) {
                    return;
                }

                if (isLockAny) {
                    log(param.method.getName() + ": Lock any.");
                    param.setResult(null);
                    return;
                }

                if (isLockOnPower && isPowered) {
                    log(param.method.getName() + ": Lock on power.");
                    param.setResult(null);
                    return;
                }

                if (isLockOnPluggedIn && Arrays.asList(param.args).contains(REASON_STRING_PLUGGED_IN)) {
                    log(param.method.getName() + ": Lock on plugged in.");
                    param.setResult(null);
                    return;
                }

                if (isFakePower && isPowered) {
                    log(param.method.getName() + ": Fake power.");
                    mIsPoweredField.setBoolean(param.thisObject, false);
                    return;
                }
            }
        });
    }

    private void hookIsPowered() {
        hookAllMethods(batterySaverStateMachineClass, "setBatteryStatus", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                reloadPreferences(param.thisObject);
                if (!isModuleEnabled) {
                    return;
                }

                if (isFakePower && isPowered) {
                    mIsPoweredField.setBoolean(param.thisObject, false);
                    return;
                }
            }
        });

        hookAllMethods(batterySaverStateMachineClass, "doAutoBatterySaverLocked", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                reloadPreferences(param.thisObject);
                if (!isModuleEnabled) {
                    return;
                }

                if (isFakePower && isPowered) {
                    log(param.method.getName() + ": Fake power.");
                    mIsPoweredField.setBoolean(param.thisObject, false);
                    return;
                }
            }
        });
    }

}