# Always Battery Saver

[![Xposed Module](https://img.shields.io/badge/Xposed%20Module-✓-green.svg)](https://github.com/icepony/AlwaysBatterySaver)
[![Android Version](https://img.shields.io/badge/Android-9.0%2B-blue.svg)](https://android.com)
[![GitHub issues](https://img.shields.io/github/issues/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/issues)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/releases/latest)

An Xposed module to prevent Android from automatically disabling Battery Saver when the device is charging.

## Problem & Solution

Android's default behavior disables Battery Saver upon charging. This module intercepts specific system calls within the Android System Server (`android` package) to prevent this automatic deactivation, allowing Battery Saver to remain active even while plugged in.

## Technical Overview

The module hooks methods within [`com.android.server.power.batterysaver.BatterySaverStateMachine`](https://github.com/aosp-mirror/platform_frameworks_base/blob/main/services/core/java/com/android/server/power/batterysaver/BatterySaverStateMachine.java):

1. **`enableBatterySaverLocked` (all variants):**

    * `lock_on_plugged_in`: Checks if method arguments contain `REASON_STRING_PLUGGED_IN` (`"Plugged in"`).
    * `lock_on_power`: Uses reflection to check the current state of the `mIsPowered` field within the `BatterySaverStateMachine` instance.
    * `lock_any`: Unconditionally blocks the method call.

2. **`updateStateLocked`:**

    * `fake_power`: If enabled, uses reflection to set the `mIsPowered` field of the `BatterySaverStateMachine` instance to `false` *before* the original method runs, effectively hiding the charging status from the state machine's internal logic.

**Note:** This module relies on specific class/method names and the `mIsPowered` field name within AOSP. Significant changes by OEMs or in future Android versions could impact functionality.

## Known Issues

In some cases, other system components or features may change the Battery Saver state without going through `BatterySaverStateMachine`. This can cause the internal state of the system to become inconsistent with the actual Battery Saver state.

For example, a feature that automatically disables Battery Saver when the battery reaches 90% may change the state independently of `BatterySaverStateMachine`. When this happens, the system may end up in an inconsistent state where Battery Saver cannot be disabled normally through Settings or other system UI.

Usually, a **reboot** is enough to restore the Battery Saver state to a consistent state.

Alternatively, you can try manually resetting the Battery Saver mode from a shell:

```sh
cmd power set-mode 0
```

or

```sh
cmd power set-mode 1
```

Depending on the device and Android version, **you may need to run these commands multiple times, or repeatedly switch between `0` and `1`,** before the Battery Saver state is restored to a consistent state.

## More Ideas

* ~~**Settings UI:** Add a simple configuration UI to toggle the module's functionality or potentially add more options later.~~
* **UI Unlocking:** Hook the Battery Saver settings page and the Quick Settings tile to prevent them from becoming disabled or grayed out while the device is charging and Battery Saver is forced on by this module.
* **Extend Support to Android 5.0 (Lollipop):** Power Saving Mode seems to be present in Android since API level 21, so supporting older devices might be feasible. This would require investigating compatibility with the `BatterySaverStateMachine` class or an equivalent implementation in those versions.

## Thanks

* Xposed Framework Developers
* [CorePatch](https://github.com/LSPosed/CorePatch) (Inspiration for hook structure)
* LLMs (Gemini, DeepSeek, ChatGPT) for assistance.

---

*Disclaimer: Use Xposed modules responsibly. Modifying system behavior carries inherent risks.*
