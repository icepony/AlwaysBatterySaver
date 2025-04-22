# Always Battery Saver

[![Xposed Module](https://img.shields.io/badge/Xposed%20Module-✓-green.svg)](https://github.com/icepony/AlwaysBatterySaver)
[![Android Version](https://img.shields.io/badge/Android-9.0%2B-blue.svg)](https://android.com)
[![GitHub issues](https://img.shields.io/github/issues/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/issues)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/releases/latest)

An Xposed module to prevent Android from automatically disabling Battery Saver when the device is charging.

## Problem & Solution

Android's default behavior disables Battery Saver upon charging. This module intercepts specific system calls within the Android System Server (`android` package) to prevent this automatic deactivation, allowing Battery Saver to remain active even while plugged in.

## Technical Overview

The module hooks methods within `com.android.server.power.batterysaver.BatterySaverStateMachine`:

1.  **`enableBatterySaverLocked` (all variants):**
    *   Hooks all methods with this name using `XposedBridge.hookAllMethods`.
    *   The `beforeHookedMethod` callback checks configured options and can prevent the original method execution (`param.setResult(null)`):
        *   `lock_on_plugged_in`: Checks if method arguments contain `REASON_STRING_PLUGGED_IN` ("Plugged in").
        *   `lock_on_power`: Uses reflection to check the current state of the `mIsPowered` field within the `BatterySaverStateMachine` instance.
        *   `lock_any`: Unconditionally blocks the method call.

2.  **`updateStateLocked`:**
    *   Hooks this method using `XposedBridge.hookAllMethods`.
    *   The `beforeHookedMethod` callback modifies internal state:
        *   `fake_power`: If enabled, uses reflection to set the `mIsPowered` field of the `BatterySaverStateMachine` instance to `false` *before* the original method runs, effectively hiding the charging status from the state machine's internal logic.

**Note:** This module relies on specific class/method names and the `mIsPowered` field name within AOSP. Significant changes by OEMs or in future Android versions could impact functionality.

## Future Ideas (Maybe!)

I have some ideas for future enhancements, but these are not guaranteed:

*   ~~**Settings UI:** Add a simple configuration UI to toggle the module's functionality or potentially add more options later.~~
*   **UI Unlocking:** Hook the Battery Saver settings page and the Quick Settings tile to prevent them from becoming disabled or grayed out while the device is charging and Battery Saver is forced on by this module.
*   **Extend Support to Android 5.0 (Lollipop):** Power Saving Mode seems to be present in Android since API level 21, so supporting older devices might be feasible. This would require investigating compatibility with the `BatterySaverStateMachine` class or equivalent in those versions.

## Troubleshooting / Compatibility

*   Functionality depends on AOSP class/method/field names (`BatterySaverStateMachine`, `enableBatterySaverLocked`, `updateStateLocked`, `mIsPowered`). Heavy OEM customization or future Android changes may break hooks.
*   Conflicts with other power-management Xposed modules are possible, though less likely given the specific target.
*   If Battery Saver still turns off, double-check module activation, scope (`Android System`) or (`System Framework`), and reboot. Check Xposed logs for errors related to `AlwaysBatterySaver`.
*   Report issues with logs on the [Issues](https://github.com/icepony/AlwaysBatterySaver/issues) page.

## Contribution

Contributions (issues, pull requests) are welcome, especially regarding compatibility improvements, hook refinements, and testing across different ROMs/versions.

## Check Out My Other Project!

*   **[AlwaysCreateUser](https://github.com/icepony/AlwaysCreateUser)**: An Xposed Framework module that bypasses Android's user/profile creation limits, if you like system-level app cloning or isolation

## Like the Project?

A small reaction (👍/❤️) on the [Releases](https://github.com/icepony/AlwaysBatterySaver/releases) page helps!

## Thanks

*   Xposed Framework Developers
*   [CorePatch](https://github.com/LSPosed/CorePatch) (Inspiration for hook structure)
*   LLMs (Gemini, DeepSeek, ChatGPT) for assistance.

---
*Disclaimer: Use Xposed modules responsibly. Modifying system behavior carries inherent risks.*