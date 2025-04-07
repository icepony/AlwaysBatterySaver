# Always Battery Saver
[![Xposed Module](https://img.shields.io/badge/Xposed%20Module-✓-green.svg)]()
[![Android Version](https://img.shields.io/badge/Android-9.0%2B-blue.svg)]()
[![GitHub issues](https://img.shields.io/github/issues/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/issues)


Keep Android's Battery Saver mode enabled, even when your device is charging.

## Introduction

Android's default behavior is to automatically disable Battery Saver mode as soon as the device is plugged in and charging. While this makes sense for most users, there might be scenarios where you *want* to keep Battery Saver active even while charging (e.g., using a very slow charger, wanting to maintain consistent low-power behavior, specific testing needs).

**Always Battery Saver** is a simple Xposed module that intercepts the system call responsible for disabling Battery Saver due to charging and prevents it, effectively keeping Battery Saver active if you enabled it manually.

## How it Works (Technical Details)

This module uses the Xposed Framework to modify the behavior of the Android system server (`android` package).

1.  **Target:** The module hooks into the `com.android.server.power.batterysaver.BatterySaverStateMachine` class within the Android system server process.
2.  **Method Hook:** It specifically targets the `enableBatterySaverLocked(boolean enable, boolean manual, int reason, String reasonStr)` method.
3.  **Interception Logic:**
    *   The module examines the parameters passed to `enableBatterySaverLocked` *before* the original method runs.
    *   It checks if the method is being called to *disable* Battery Saver (`enable` parameter is `false`).
    *   It checks if the reason for disabling is that the device was plugged in (`reason` parameter is `7`, corresponding to `REASON_PLUGGED_IN`).
    *   If both conditions are true (trying to disable Battery Saver *because* it's plugged in), the module prevents the original `enableBatterySaverLocked` method from executing (`param.setResult(null)`).
4.  **Result:** Battery Saver mode remains enabled even when the device starts charging, provided it was already manually enabled.

## Requirements

*   Rooted Android Device
*   Xposed Framework installed and active (e.g., LSPosed, EdXposed, original Xposed). Compatibility may vary depending on your Android version and ROM.

## Installation

1.  Download the latest APK from the [Releases](https://github.com/icepony/AlwaysBatterySaver/releases) page.
2.  Install the APK.
3.  Open your Xposed manager app (e.g., LSPosed).
4.  Enable the **AlwaysBatterySaver** module. Ensure it's enabled for the `Android System` (or `system_server`) scope.
5.  Reboot your device.
6.  Manually enable Battery Saver. It should now stay enabled even when you plug your device in.

## Future Ideas (Maybe!)

I have some ideas for future enhancements, but these are not guaranteed:

*   **Settings UI:** Add a simple configuration UI to toggle the module's functionality or potentially add more options later.
*   **UI Unlocking:** Hook the Battery Saver settings page and the Quick Settings tile to prevent them from becoming disabled or grayed out while the device is charging and Battery Saver is forced on by this module.

## Troubleshooting / Compatibility

*   This module modifies core system behavior. Conflicts with other power-management Xposed modules *might* occur, though unlikely given its specific target.
*   The targeted class (`BatterySaverStateMachine`) and method (`enableBatterySaverLocked`) could potentially change in future Android versions or heavily modified custom ROMs, which might break the module's functionality. If you encounter issues, please report them on the [Issues](https://github.com/icepony/AlwaysBatterySaver/issues) page.

## Check Out My Other Project!

If you find system modifications useful, you might also like:

*   **[AlwaysCreateUser](https://github.com/icepony/AlwaysCreateUser)**: An Xposed Framework module that bypasses Android's user/profile creation limits, if you like system-level app cloning or isolation

## Like the Project?

If this module helps you out, I'd be really happy if you could visit the [Releases](https://github.com/icepony/AlwaysBatterySaver/releases) page and leave a reaction (like 👍 or ❤️) on the version you downloaded! just a small reaction on the release makes my day. 😊

## Thanks

- [Gemini](https://gemini.google.com/app)
- [DeepSeek](https://www.deepseek.com/)
- [ChatGPT](https://chatgpt.com/)
- [CorePatch](https://github.com/LSPosed/CorePatch)

