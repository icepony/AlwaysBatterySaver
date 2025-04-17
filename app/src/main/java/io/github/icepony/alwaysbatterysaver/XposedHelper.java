package io.github.icepony.alwaysbatterysaver;

import static io.github.icepony.alwaysbatterysaver.MainHook.TAG;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XposedHelper {
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
        if (BuildConfig.DEBUG) {
            XposedBridge.log("D/" + TAG + ": " + message);
        }
    }
}
