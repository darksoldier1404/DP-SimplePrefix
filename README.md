![](https://dpnw.site/assets/img/logo_white.png)

![](https://dpnw.site/assets/img/desc_card/dppcore.jpg)

# All DP-Plugins depend on the [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core) plugin. <br>Please make sure to install [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core)

# Discord
### Join our Discord server to get support and stay updated with the latest news and updates.

### If you have any questions or suggestions, please join our Discord server.

### If you find any bugs, please report them using the inquiry channel.

<span style="font-size: 18px;">**Discord Invite: https://discord.gg/JnMCqkn2FX**</span>

<br>
<br>

<details>
	<summary>korean</summary>

![](https://dpnw.site/assets/img/desc_card/desc.jpg)

# DP-SimplePrefix 플러그인 소개
DP-SimplePrefix는 마인크래프트 서버에서 칭호를 쉽게 관리할 수 있는 플러그인입니다. GUI를 통해 칭호 목록을 직관적으로 확인하고, 간편한 칭호 쿠폰 설정이 가능하며, LuckPerms의 칭호 노드와 연동하여 칭호를 효율적으로 관리할 수 있습니다.

## 플러그인 특징
- **GUI 기반 칭호 목록**: GUI를 통해 쉽게 확인 가능.
- **간편한 쿠폰 설정**: 특정 칭호 쿠폰을 간단히 설정 가능.
- **LuckPerms 연동**: LuckPerms의 칭호 노드와 연동되어 칭호를 효율적으로 관리 가능.
- **기본 칭호 설정**: 모든 플레이어에게 자동으로 부여되는 기본 칭호 설정 가능.
- **유저 친화적 명령어**: 플레이어가 칭호를 장착, 해제, 확인할 수 있는 간단한 명령어 제공.

## 의존성
- DPP-Core
- LuckPerms
- Vault

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/cmd-perm.jpg)

## 명령어
### 관리자 명령어
| 명령어 | 설명 |
|--------|------|
| `/dsp create <prefix>` | 새로운 칭호를 생성합니다. |
| `/dsp set <prefix>` | 채팅에 표시될 칭호를 설정합니다. |
| `/dsp delete <prefix>` | 지정한 칭호를 삭제합니다. |
| `/dsp coupon <prefix>` | 지정한 칭호의 쿠폰 아이템을 설정합니다. (칭호 미입력 시 글로벌 쿠폰으로 설정) |
| `/dsp givecoupon <prefix> [username]` | 지정한 플레이어에게 칭호 쿠폰 아이템을 지급합니다. (username 미입력 시 명령어 실행자에게 지급) |
| `/dsp default <prefix>` | 모든 플레이어에게 부여될 기본 칭호를 설정합니다. |
| `/dsp list` | 모든 칭호 목록을 표시합니다. |

### 유저 명령어
| 명령어 | 설명 |
|--------|------|
| `/dsp equip <prefix>` | 지정한 칭호를 장착합니다. |
| `/dsp unequip` | 현재 장착된 칭호를 해제합니다. |
| `/dsp my` | 자신이 소유한 모든 칭호를 표시합니다. |

## 사용법 예시
- 칭호 생성: `/dsp create VIP`
- 채팅 칭호 설정: `/dsp set VIP`
- 칭호 삭제: `/dsp delete VIP`
- 칭호 쿠폰 설정: `/dsp coupon VIP`
- 플레이어에게 쿠폰 지급: `/dsp givecoupon VIP Steve`
- 기본 칭호 설정: `/dsp default Member`
- 칭호 장착 (유저): `/dsp equip VIP`
- 칭호 해제 (유저): `/dsp unequip`
- 소유 칭호 확인 (유저): `/dsp my`

</details>

<details open>
	<summary>english</summary>

![](https://dpnw.site/assets/img/desc_card/desc.jpg)

# DP-SimplePrefix Plugin Introduction

DP-SimplePrefix is a Minecraft plugin designed to manage prefixes in a server with ease. It provides a user-friendly GUI for prefix management, simple coupon configuration for prefixes, and integration with LuckPerms' prefix nodes for efficient prefix management.

## Plugin Features
- **GUI Prefix List**: View prefixes with GUI.
- **Coupon Configuration**: Easily set up coupon items for specific prefixes.
- **LuckPerms Integration**: Integrates with LuckPerms' prefix nodes for efficient prefix management.
- **Default Prefix Support**: Set a default prefix to be automatically assigned to all players.
- **Player-Friendly Commands**: Simple commands for players to equip, unequip, or view their prefixes.

## Dependencies
- DPP-Core
- LuckPerms
- Vault

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/cmd-perm.jpg)

## Commands
### Admin Commands
| Command | Description |
|---------|-------------|
| `/dsp create <prefix>` | Creates a new prefix. |
| `/dsp set <prefix>` | Sets the prefix to be displayed in chat. |
| `/dsp delete <prefix>` | Deletes a specified prefix. |
| `/dsp coupon <prefix>` | Sets a coupon item for the specified prefix (if no prefix is provided, sets a global coupon). |
| `/dsp givecoupon <prefix> [username]` | Gives the coupon item for a prefix to a specified player (or the command executor if no username is provided). |
| `/dsp default <prefix>` | Sets a default prefix to be given to all players. |
| `/dsp list` | Lists all available prefixes. |

### Player Commands
| Command | Description |
|---------|-------------|
| `/dsp equip <prefix>` | Equips a specified prefix for the player. |
| `/dsp unequip` | Unequips the currently active prefix. |
| `/dsp my` | Displays all prefixes owned by the player. |

## Usage Examples
- Create a prefix: `/dsp create VIP`
- Set a chat prefix: `/dsp set VIP`
- Delete a prefix: `/dsp delete VIP`
- Set a coupon for a prefix: `/dsp coupon VIP`
- Give a coupon to a player: `/dsp givecoupon VIP Steve`
- Set a default prefix: `/dsp default Member`
- Equip a prefix (player): `/dsp equip VIP`
- Unequip a prefix (player): `/dsp unequip`
- View owned prefixes (player): `/dsp my`

</details>

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/screenshot.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-SimplePrefix/1.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-SimplePrefix/2.jpg)
