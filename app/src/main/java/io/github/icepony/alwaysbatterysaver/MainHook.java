package io.github.icepony.alwaysbatterysaver;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook extends XposedHelper implements IXposedHookLoadPackage {

    public static final String TAG = "AlwaysBatterySaver";

    private static final String BATTERY_SAVER_STATE_MACHINE_CLASS = "com.android.server.power.batterysaver.BatterySaverStateMachine";
    private static final int REASON_CODE_PLUGGED_IN = 7;
    private static final String REASON_STRING_PLUGGED_IN = "Plugged in";

    XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID);
    boolean isModuleEnable, isBlockSetBatteryStatus, isBlockEnableBatterySaverOnCharge, isBlockEnableBatterySaverAny;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            log("Initial module, enabled state: " + prefs.getBoolean("enable_module", true));

            hookBatteryStatusSetter(lpparam);
            hookBatterySaverEnabler(lpparam);
        }
    }

    private void hookBatteryStatusSetter(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(BATTERY_SAVER_STATE_MACHINE_CLASS,
                lpparam.classLoader,
                "setBatteryStatus",
                boolean.class, int.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        prefs.reload();
                        isModuleEnable = prefs.getBoolean("enable_module", true);
                        isBlockSetBatteryStatus = prefs.getBoolean("block_setBatteryStatus", false);

                        if (isModuleEnable) {
                            boolean newPowered = (boolean) param.args[0];
                            if (newPowered && isBlockSetBatteryStatus) {
                                log("Intercepted: Set charging status");
                                param.setResult(null);
                            }
                        }
                    }
                });
    }

    private void hookBatterySaverEnabler(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(BATTERY_SAVER_STATE_MACHINE_CLASS,
                lpparam.classLoader,
                "enableBatterySaverLocked",
                boolean.class, boolean.class, int.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        prefs.reload();
                        isModuleEnable = prefs.getBoolean("enable_module", true);
                        isBlockEnableBatterySaverOnCharge = prefs.getBoolean("block_enableBatterySaver_on_charge", false);
                        isBlockEnableBatterySaverAny = prefs.getBoolean("block_enableBatterySaver_any", false);

                        if (isModuleEnable) {
                            if (isBlockEnableBatterySaverAny) {
                                log("Intercepted: change battery saver");
                                param.setResult(null);
                            } else {
                                int intReason = (int) param.args[2];
                                String strReason = (String) param.args[3];
                                if (intReason == REASON_CODE_PLUGGED_IN
                                        && strReason.equals(REASON_STRING_PLUGGED_IN)
                                        && isBlockEnableBatterySaverOnCharge) {
                                    log("Intercepted: Automatically disable battery saver when charging");
                                    param.setResult(null);
                                }
                            }
                        }
                    }
                });
    }
}