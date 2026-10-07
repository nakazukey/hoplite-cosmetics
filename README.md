# Hoplite VIP

Client-side Minecraft **1.21.11 Fabric** mod by **Nakazukey**. Current version: **1.3.2**.

## Features

- VIP badge and name color in TAB, third person, chat and supported server displays.
- 39 color choices, including the supplied Hoplite badges and custom gradients.
- Solid name colors for every option except Rainbow.
- White messages for your own chat, with styled join/leave notifications.
- Your player appears first in TAB.
- Plain green name without VIP in TAB and third person during detected Duel/UHC matches.
- Optional brighter pink rose crown, raised by two model pixels.
- English settings menu with saved preferences.

Changes are local to your client. They do not change the server rank or what other players see.

## Install

Select Minecraft 1.21.11 with Lunar + Fabric, remove any older version of the mod, and import `hoplite-vip-1.21.11-1.3.2.jar` in the profile's Mods section. Restart Minecraft.

Requires **Java 21+** and **Fabric Loader 0.18.2+**. The required Fabric API modules are bundled.

Press **Numpad Minus (-)** to open the menu. Change the binding under **Options → Controls → Key Binds → Hoplite VIP**. Use the menu to select a color, enable the crown or disable the mod. Preferences are saved in `config/vip-local.json`.

## Build

Set `JAVA_HOME` to a JDK 21 or newer installation.

Windows:

```powershell
.\gradlew.bat build
```

Linux/macOS:

```sh
chmod +x gradlew
./gradlew build
```

The installable JAR is generated in `build/libs/`. The sources JAR is not an installable mod. The build also runs the checks in `VipStyleCheck`.

## Code layout

| File | Purpose |
| --- | --- |
| `VipClient` | Startup and menu key |
| `VipSettings` | Colors and saved preferences |
| `VipScreen` | Settings menu |
| `VipStyle` | Badge, name and chat formatting |
| `VipMatch` | Match detection and green names |
| `VipOrder` | TAB ordering |
| `mixin/` | Minecraft rendering hooks |
| `src/main/resources/` | Textures, fonts, equipment and mod metadata |
| `src/test/` | Formatting and behavior checks |

## Compatibility

The user confirmed version 1.3.2 works on their Lunar setup. Server format changes or different rendering mods may need adjustments. Unsigned player chat is recognized by sender prefixes; unusual formats may require a specific rule. Match detection uses server team/name colors and scoreboard titles.

The rose crown uses the supplied armor texture with the chestplate region removed. It replaces only the local player's rendered helmet, without changing the actual equipment or armor statistics.

## Credits

Author: **Nakazukey**. Badge references and the crown texture were supplied by the user. The icon depicts a hoplite helmet with a pink plume and VIP badge. The internal ID remains `vip_local` for configuration compatibility.

## Upload to GitHub

Create an empty repository, then run these commands inside this project folder, replacing the URL with your repository URL:

```sh
git init -b main
git add .
git commit -m "Initial Hoplite VIP project"
git remote add origin https://github.com/YOUR_USERNAME/Hoplite-VIP.git
git push -u origin main
```

If Git requests an author identity, configure your own name and email before committing. The included `.gitignore` excludes generated builds, development caches and logs. The Gradle Wrapper JAR is intentionally included.

To distribute the compiled mod, attach the installable JAR to a GitHub Release.
