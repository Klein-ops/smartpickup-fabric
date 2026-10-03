# Smart Pickup (Fabric)

**智能拾取** — an intelligent item pickup mod for Minecraft **Fabric 1.21.10**.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## ✨ Features / 功能

| Feature | Description |
|---|---|
| **Pickup to inventory first** / 拾取优先进背包 | Picked-up items fill the main inventory before free hotbar slots (hotbar-priority whitelist keeps vanilla order for chosen items). |
| **Auto refill** / 自动补充 | Automatically refill an emptied or broken held stack from the inventory (each sub-scenario independently toggleable). |
| **Blacklist** / 黑名单 | Refuse pickup of specific items via an in-game item picker UI. |
| **Pickup toast** / 拾取提示 | Stacked HUD entries above the hotbar showing what you picked up (with a configurable merge window). |

Everything is controlled by a master switch plus per-feature toggles — when a feature is off, vanilla behaviour applies.

---

## 📦 Requirements / 环境要求

- Minecraft **1.21.10**
- Fabric Loader **>= 0.19.5**
- Fabric API **>= 0.138.4**
- Java **21**
- (Optional) [ModMenu](https://modrinth.com/mod/modmenu) **16.0.1+** — needed for the in-game config screen

---

## 🚀 Installation / 安装

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.10.
2. Put the `.jar` from [Releases](https://github.com/Klein-ops/smartpickup/releases) into your `mods/` folder.
3. Also install Fabric API (and ModMenu if you want the config screen).
4. Launch the game — open the config screen via ModMenu → **智能拾取 / Smart Pickup**.

> Multiplayer note: the mod works on both client and server. In a dedicated server,
> install the jar on the server too (the server performs the smart pickup logic).

---

## 🎮 Config / 配置

The config file is created at `config/smartpickup.json` on first launch
(self-contained JSON, no extra config library needed).

| Key | Default | Meaning |
|---|---|---|
| `masterEnabled` | `true` | Master switch for the whole mod |
| `pickupEnabled` | `true` | Smart pickup order (inventory before hotbar) |
| `hotbarPriorityEnabled` | `false` | Hotbar-priority whitelist (vanilla order for those items) |
| `refillEnabled` | `true` | Auto refill |
| `refillOnEmpty` | `true` | Refill when the held stack is used up |
| `refillOnBreak` | `true` | Refill when the held tool breaks |
| `refillSkipCreative` | `true` | Never refill in Creative mode |
| `blacklistEnabled` | `true` | Blacklist feature |
| `toastEnabled` | `true` | Pickup toast HUD |
| `toastDuration` | `3.0` | Toast lifetime (seconds) |
| `toastMergeWindow` | `1.5` | Merge window for consecutive pickups of the same item (seconds) |
| `toastBlacklistRejected` | `true` | Show a toast when a blacklisted item is refused |

You can also edit the blacklist / hotbar-priority lists in the in-game config screen.

---

## 🛠 Building from source / 从源码构建

```bash
# Java 21 + Gradle 8.14.2+
./gradlew build
# or if you have a local gradle installation:
gradle build
```

The built jar lands in `build/libs/smartpickup-fabric-<version>.jar`.

---

## 🧩 Architecture / 架构

- **Server / common** (`src/main`): pickup interception via a Mixin on
  `ItemEntity.playerTouch`, auto-refill via per-player tick snapshots and a
  Mixin on `ServerPlayer.onEquippedItemBroken`, JSON config, custom payloads.
- **Client** (`src/client`): screen-state sync, pickup toast manager +
  custom HUD renderer, ModMenu config screen and item-picker UIs.

---

## 📄 License / 开源协议

[MIT](LICENSE) © Klein-ops

---

*Also available: [NeoForge 1.21.1 version](https://github.com/Klein-ops/smartpickup)*
