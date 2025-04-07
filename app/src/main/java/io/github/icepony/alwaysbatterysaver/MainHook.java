package io.github.icepony.alwaysbatterysaver;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TAG = "AlwaysBatterySaver";

    private static final int REASON_PLUGGED_IN = 7;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            log("Handling Android package");

            log("Hooking BatterySaverStateMachine...");
            hookBatterySaverStateMachine(lpparam);
        }

    }

    private void hookBatterySaverStateMachine(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.android.server.power.batterysaver.BatterySaverStateMachine", lpparam.classLoader, "enableBatterySaverLocked", boolean.class, boolean.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                boolean enable = (boolean) param.args[0];
                int reason = (int) param.args[2];

                log("Hooking enableBatterySaverLocked: enable=" + enable + ", manual=" + param.args[1] + ", reason=" + reason + ", reasonStr=" + param.args[3]);
//                log("Call stack:\n" + Log.getStackTraceString(new Throwable()));

                if (!enable && reason == REASON_PLUGGED_IN) {
                    log("Intercepted: Trying to disable battery saver due to charging (Reason " + reason + "). Preventing method execution.");
                    param.setResult(null);
                }
            }
        });
    }

    public static void findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
            log("Successfully added hook for " + className + "#" + methodName);
        } catch (Throwable e) {
            logError("Error hook method: " + className + "#" + methodName, e);
        }
    }

    public static void hookAllMethods(String className, ClassLoader classLoader, String methodName, XC_MethodHook callback) {
        try {
            Class<?> clazz = findClass(className, classLoader);
            XposedBridge.hookAllMethods(clazz, methodName, callback);
            log("Successfully added hook for " + className + "#" + methodName);
        } catch (Throwable e) {
            logError("Error hook method: " + className + "#" + methodName, e);
        }
    }

    public static Class<?> findClass(String className, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(className, classLoader);
        } catch (Throwable e) {
            logError("Error finding class: " + className, e);
        }
        return null;
    }

    public static void logError(String message, Throwable t) {
        XposedBridge.log("E/" + TAG + ": " + message + "\n" + Log.getStackTraceString(t));
    }

    public static void log(String message) {
        XposedBridge.log("D/" + TAG + ": " + message);
    }
}