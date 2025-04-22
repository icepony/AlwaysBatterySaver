package io.github.icepony.alwaysbatterysaver;

import java.lang.reflect.Field;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook extends XposedHelper implements IXposedHookLoadPackage {
    public static final int REASON_CODE_PLUGGED_IN = 7;
    public static final String REASON_STRING_PLUGGED_IN = "Plugged in";

    private boolean isModuleEnabled;
    private boolean isFakePower;
    private boolean isLockOnPluggedIn;
    private boolean isLockOnPower;
    private boolean isLockAny;

    private void reloadPreferences() {
        prefs.reload();
        isModuleEnabled = prefs.getBoolean("enable_module", true);
        isLockOnPluggedIn = prefs.getBoolean("lock_on_plugged_in", true);
        isLockOnPower = prefs.getBoolean("lock_on_power", false);
        isFakePower = prefs.getBoolean("fake_power", false);
        isLockAny = prefs.getBoolean("lock_any", false);

    }

    private Class<?> batterySaverStateMachineClass;
    private Field mIsPowered;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            reloadPreferences();
            log("Initializing module hook for Android process. Module enabled preference: " + isModuleEnabled);

            batterySaverStateMachineClass = findClass("com.android.server.power.batterysaver.BatterySaverStateMachine", lpparam.classLoader);
            mIsPowered = findField(batterySaverStateMachineClass, "mIsPowered");

            hookIsPowered();
            hookBatterySaverEnabler();
        }
    }

    private void hookBatterySaverEnabler() {
        hookAllMethods(batterySaverStateMachineClass, "enableBatterySaverLocked", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                reloadPreferences();
                if (!isModuleEnabled) {
                    return;
                }

                if (isLockAny) {
                    log(param.method.getName() + ": Lock any.");
                    param.setResult(null);
                    return;
                }

                if (isLockOnPower) {
                    if (mIsPowered.getBoolean(param.thisObject)) {
                        log(param.method.getName() + ": Lock on power.");
                        param.setResult(null);
                    }
                }

                if (isLockOnPluggedIn) {
//                    boolean isPluggedIn = Arrays.stream(param.args).anyMatch(o -> o.equals(REASON_CODE_PLUGGED_IN) && o.equals(REASON_STRING_PLUGGED_IN));
                    boolean isPluggedIn = Arrays.asList(param.args).contains(REASON_STRING_PLUGGED_IN);
                    if (isPluggedIn) {
                        log(param.method.getName() + ": Lock on plugged in.");
                        param.setResult(null);
                    }
                }
            }
        });
    }

    private void hookIsPowered() {
//        hookAllMethods(batterySaverStateMachineClass, "setBatteryStatus", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                reloadPreferences();
//                if (!isModuleEnabled) {
//                    return;
//                }
//
//                if (isFakePower) {
//                    boolean newPowered = (boolean) param.args[0];
//                    if (newPowered) {
//                        param.setResult(null);
//                    }
//                }
//            }
//        });

        hookAllMethods(batterySaverStateMachineClass, "updateStateLocked", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                reloadPreferences();
                if (!isModuleEnabled) {
                    return;
                }

                if (isFakePower) {
                    log(param.method.getName() + ": Fake power.");
                    mIsPowered.setBoolean(param.thisObject, false);
                }
            }
        });
    }

}