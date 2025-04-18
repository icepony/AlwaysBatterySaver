# Always Battery Saver
[![Xposed Module](https://img.shields.io/badge/Xposed%20Module-✓-green.svg)]()
[![Android Version](https://img.shields.io/badge/Android-9.0%2B-blue.svg)]()
[![GitHub issues](https://img.shields.io/github/issues/icepony/AlwaysBatterySaver)](https://github.com/icepony/AlwaysBatterySaver/issues)


Keep Android's Battery Saver mode enabled, even when your device is charging.

## Introduction

Android's default behavior is to automatically disable Battery Saver mode as soon as the device is plugged in and charging. While this makes sense for most users, there might be scenarios where you *want* to keep Battery Saver active even while charging (e.g., using a very slow charger, wanting to maintain consistent low-power behavior, specific testing needs).

**Always Battery Saver** is a simple Xposed module that intercepts the system call responsible for disabling Battery Saver due to charging and prevents it, effectively keeping Battery Saver active if you enabled it manually.

## Core Logic

This module works by hooking into the Android system server (`android` package), specifically targeting methods within the `com.android.server.power.batterysaver.BatterySaverStateMachine` class (or dynamically finding similar methods).

It intercepts system calls related to Battery Saver state changes:

1.  **`enableBatterySaverLocked`:**
    *   Prevents Battery Saver from being automatically disabled when the device is plugged in (`reason = 7 / "Plugged in"`), if the corresponding option is enabled.
    *   (Experimental) Can block *all* calls to this method to lock the current Battery Saver state.
2.  **`setBatteryStatus`:**
    *   Can prevent the system from notifying the Battery Saver service that the device is charging (`newPowered = true`), effectively making the system think it's always on battery.

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

*   ~~**Settings UI:** Add a simple configuration UI to toggle the module's functionality or potentially add more options later.~~
*   **UI Unlocking:** Hook the Battery Saver settings page and the Quick Settings tile to prevent them from becoming disabled or grayed out while the device is charging and Battery Saver is forced on by this module.
*   **Extend Support to Android 5.0 (Lollipop):** Power Saving Mode seems to be present in Android since API level 21, so supporting older devices might be feasible. This would require investigating compatibility with the `BatterySaverStateMachine` class or equivalent in those versions.

## Troubleshooting / Compatibility

*   This module modifies core system behavior. Conflicts with other power-management Xposed modules *might* occur, though unlikely given its specific target.
*   The targeted class (`BatterySaverStateMachine`) and method (`enableBatterySaverLocked`) could potentially change in future Android versions or heavily modified custom ROMs, which might break the module's functionality. If you encounter issues, please report them on the [Issues](https://github.com/icepony/AlwaysBatterySaver/issues) page.

## Contribution

Contributions are welcome! Feel free to open issues or submit pull requests. Key areas for improvement include refining hooks, implementing dynamic method finding, and testing across various devices/ROMs.

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

---
*Disclaimer: Modifying system behavior with Xposed can potentially lead to instability. Use at your own risk.*
