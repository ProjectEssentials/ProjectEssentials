# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Since 2.0.0 versions change log same for all supported minecraft versions.

## [Unreleased]

## [2.0.1] - 2020-06-20

### Added
- Chinese localization by @KuroNoSeiHai.

### Fixed
- Grammar mistake fixed in default kit configuration.

## [2.0.0] - 2020-06-09

### Added
- Ability to give own kit to other player.
- Kit command now suggests available kits.
- Lightweight user manager implemented.
- Localization for some teleport commands added.

### Changed
- `withServerPlayer` now is an inline.
- core version updated to 2.0.3.
- `pack.mcmeta`, `updatev2.json`, `readme.md` updated.
- core dependency now is not transitive.
- `KitManager.kt` formatted.
- Ping command out message improved.
- Kit manager re-written.
- Source code cleanup.

### Fixed
- Ext command wrong out a message fixed.
- Incorrect module name fixed.
- Afk detect still has incorrect behavior.
- Repair command no fixes current item.
- Platform status command out fixed.
- `UserDataConfiguration.kt` incorrect saving fixed.
- `KitsConfiguration.kt` incorrect saving fixed.
- Kit saved date parsing bug fixed.
- Incorrect server time (in `platform-status`) fixed.
- Mistake fixed in `TpaHereCommand.kt`.

### Removed
- `processPlayerTabNick` removed.
- `brigadier` removed from dependencies.
- `brigadier_version` property removed from gradle.properties.

## [2.0.0-SNAPSHOT.1] - 2020-05-30

### Added
- **Fully re-written all code of this module.**
- Settings constants added to `InternalConstants.kt`.
- Initializing module settings implemented.
- `disabled-commands` setting added.
- `fly-worlds-disabled` and `god-worlds-disabled` setting added.
- `UserDataConfigurationModel.kt` added.
- `UserDataConfiguration.kt` implemented.
- Message about remove user added. *(purging user data)*
- AFK state now depends on player activity.
- `validateAndExecute` added in `InternalHelpers.kt`.
- `onPlayerUpdate` added with handling afk state in `ModuleObject.kt`.
- Canceling afk state for player after player leaving. Closes #16.
- Auto fly mode feature re-implemented.
- Auto god mode feature re-implemented.
- Commands on first join implemented. Closes #11.
- `lastWorldName` added to `User` model.
- Kit manager implemented.
- `KitConfiguration.kt` implemented.
- Kicking player when player stays long afk.
- Permission `ess.afk.kick.bypass` checking added.
- `/enderchest` command implemented.
- `/invsee` command implemented.
- `/glow` command implemented.
- `/vanish` command implemented.
- `/workbench` command implemented.
- `invsee-disable-danger-slots` setting added.
- `/skull` command implemented.
- `/platform-status` command implemented.
- `/jump` command implemented.
- `/extingush` (`ext`) command implemented.

### Changed
- `CommandAliases#searchForAliases` moved to InternalHelpers.kt/`isAliasesOfBlockedCommand`.
- Configuration paths, user configuration now not stores in `user-data/<uuid>/...` folder.
- AFK manager re-written.
- Teleport manager re-written, but fixed nothing lol, just code cleanup.
- Localization updated for all commands and events.
- Improved `switchFly` code.
- Help command make able to use without page argument.
- All commands re-written.
- Ping command out improved.
- SendPos command out improved.
- fly\god:`Worlds` renamed to :`Dimensions`
- Handling blocked command re-written.
- Saving player data re-written.
- Commands which has format like `tpaccept` renamed to `tp-accept` (splitted via `-`).

### Fixed
- Incorrectly saved afk state on a player re-join.
- Incorrect suicide command out when v2 localization enabled fixed.

### Removed
- Weather and time commands removed.
- `GetPosCommand.kt` removed.
- Internal `CommandsAPI.kt` removed.
- Unused configuration fields removed from `CommandsConfig.kt`.
- `CommandsConfig.kt` and `ModConfiguration.kt` removed.
- Old `CommandBase.kt` removed.
- `SETTING_BURN_COMMAND_DURATION` removed.
- `EssentialsCommand.kt` removed.

## [1.15.2-1.1.2] - 2020-03-19

### Fixed
- Incorrect output with enabled localization for `afk` command.
- Incorrect output with enabled localization for `suicide` command.

## [1.14.4-1.0.2] - 2020-03-19

### Fixed
- Incorrect output with enabled localization for `afk` command.
- Incorrect output with enabled localization for `suicide` command.

## [1.15.2-1.1.1] - 2020-03-14 *Synced with 1.14.4-1.0.1*

### Added
- Project Essentials Libraries added to `libs` directory.
- Localization processing added.
- Support safe-localization added.
- Top command compatibility with back location api added.
- Tpaccept command compatibility with back location api added.
- Tpall command compatibility with back location api added.
- Tphere command compatibility with back location api added.
- Tppos command compatibility with back location api added.

### Changed
- Kotlin dependency updated.
- KotlinX Serialization dependency updated.
- Forge version updated to `28.2.0`.
- `@UseExperimental` annotation replaced with `@OptIn` in `TeleportPresenter.kt`.
- Deprecated API functions replaced on actual functions.

### Removed
- ess modules from `gradle.properties` removed.
- Ess modules removed from dependencies in `build.gradle`.
- `jitpack.io` maven repository removed from repositories in `build.gradle`.

## [1.14.4-1.0.1] - 2020-03-14

### Added
- Project Essentials Libraries added to `libs` directory.
- Localization processing added.
- Support safe-localization added.
- Top command compatibility with back location api added.
- Tpaccept command compatibility with back location api added.
- Tpall command compatibility with back location api added.
- Tphere command compatibility with back location api added.
- Tppos command compatibility with back location api added.

### Changed
- Kotlin dependency updated.
- KotlinX Serialization dependency updated.
- Forge version updated to `28.2.0`.
- `@UseExperimental` annotation replaced with `@OptIn` in `TeleportPresenter.kt`.
- Deprecated API functions replaced on actual functions.

### Removed
- ess modules from `gradle.properties` removed.
- Ess modules removed from dependencies in `build.gradle`.
- `jitpack.io` maven repository removed from repositories in `build.gradle`.

## [1.15.2-1.1.0] - 2020-02-21 *Synced with 1.14.4-1.0.0*

### Added

- `CommandsAPI.kt` implemented `getIntExisting` method.
- `CommandsAPI.kt` implemented `getInt` method.
- `CommandsAPI.kt` implemented `getDispatcher` method.
- `en_us.json` localization added for help command.
- `ru_ru.json` localization added for help command.
- `HelpCommand.kt` help command implemented.
- Color settings for `/help` command added.
- Closes #6. (Reason: Resolved).
- `/list` command implemented with pages and colors.
- New `CommandsAPI` implemented.

### Changed
- `CommandSourceExtensions.kt` sendMsg argument type args: `String` changed to `Any`.

### Fixed
- Log message in `EssentialsCommand.kt`.
- Incorrect literal for `help` command.
- #5. (Checking on CooldownAPI existing added).

## [1.14.4-1.0.0] - 2020-02-21

### Added

- `CommandsAPI.kt` implemented `getIntExisting` method.
- `CommandsAPI.kt` implemented `getInt` method.
- `CommandsAPI.kt` implemented `getDispatcher` method.
- `en_us.json` localization added for help command.
- `ru_ru.json` localization added for help command.
- `HelpCommand.kt` help command implemented.
- Color settings for `/help` command added.
- Closes #6. (Reason: Resolved).
- `/list` command implemented with pages and colors.
- New `CommandsAPI` implemented.

### Changed
- `CommandSourceExtensions.kt` sendMsg argument type args: `String` changed to `Any`.

### Fixed
- Log message in `EssentialsCommand.kt`.
- Incorrect literal for `help` command.
- #5. (Checking on CooldownAPI existing added).

## [1.15.2-1.0.0] - 2020-02-08

### Added

- Initial release.

## [1.14.4-0.3.0] - 2020-01-25

### Added

- `/tpall` command and his aliases.
- `en-US` and `ru-RU` localization for `/tpall` command.
- `/tphere` command and his aliases.
- `en-US` and `ru-RU` localization for `/tphere` command.
- `/sendpos` command.
- `/tpa`, `/tpaccept`, `/tpdeny` and `/tptoggle` commands.
- `/tpacancel` command.
- `/tpahere` command.
- `/tpahere` command compatibility with `/tpdeny` command.
- `/tpahere` command compatibility with `/tpaccept` command.
- reloading `/tpaccept`, `/tpahere`, `/tpdeny`, `/tptoggle` and `/tpacancel` commands.
- Documentation and renamed fields in `RequestedToAll` class in `TeleportPresenter.kt`.
- `/tpaall` command realization.
- Localization for `/tpaall` command.
- Localization for `/tpa` command.
- Localization for `/tpacancel` command.
- Localization for `/tpdeny` command.
- Localization for `/tptoggle` command.
- Localization for `/tpaccept` command.
- Localization for `/tpahere` command.
- Compatibility with permissions v1+ module.
- Compatibility with modules: core and cooldown.

### Changed

- Sources synchronized.
- `kotlin` and `kotlinx serialization` version updated.
- Forge version updated to `1.14.4-28.1.111`.
- `TeleportPresenter.kt` code for handling teleport requests improved.
- Bumped forge version to 28.1.114.
- Logging in `TeleportPresenter.kt` improved.
- ModConfiguration.kt code refactoring.
- Performance improved while loading aliases.
- `CommandBase.kt` logger messages improved.
- Now using `jsonInstance` from core module.
- Issue tracker and update json url.
- Mandatory dependency version range.
- Naming style for configuration fields changed.
- Gradle wrapper updated to `5.6.4`.
- `README.md` renamed to `readme.md`.

### Fixed

- Incorrect behavior with tphere requests.
- Runtime exceptions when json parser take unknown key.

### Removed

- Redundant comments from `ProjectEssentials.kt`.
- `RequestedToAll` class from `TeleportPresenter.kt`.
- Duplicate register commands.
- `ListCommand`. (replaced by vanilla list command)
- `TimeCommand`. (replaced by vanilla list command)
- `MinecraftTimeHelper.kt`.
- Redundant logger messages from `ProjectEssentials.kt`.
- `createConfigDirs` method from `StorageBase.kt`.
- Some logger messages from `StorageBase.kt`.

## [1.14.4-0.2.0 ~~.0~~] - 2019-10-27

### Added

- `/ping` command and command aliases.
- Issue templates to repository.
- Needed dependencies to `mods.toml` file.
- `AliasValidationHelper` class to `helpers` package.
- `AirCommand` checking on disabled argument.
- `CommandTimeBase` abstract class for time based commands.
- `CommandWeatherBase` class for weather based command.
- Applying aliases, and now mod can be launched without cooldown module.
- `/afk` command and his aliases with localization.
- `/burn` command and his aliases with localization.
- `/lightning` command and his aliases with localization.
- `/tppos` command and his aliases with localization.
- `en-US` and `ru-RU` localization for `/clear` command.

### Changed

- Project source code structure improved.
- `build.gradle` file and updated dependencies improved.
- `ProjectEssentials` (entry point class) refactoring.
- `UserData` data class, `StorageBase` and `CooldownsUtils` classes code refactoring.
- `ModConfiguration` class code improved.
- `CommandAliases` class code improvements.
- `FlyCommand` class code refactoring.
- Messages identifiers for `fly` command.
- `CommandBase` class code refactoring.
- `GodCommand` class code refactoring.
- Messages identifiers for `god` command.
- `BreakCommand`, `ListCommand`, `MoreCommand`, `RepairCommand`, `SuicideCommand` class code refactoring.
- `AirCommand` class code refactoring.
- `FeedCommand`, `HealCommand`, `GetPosCommand`, `TopCommand` class code refactoring.
- All time based commands refactoring.
- All weather based commands refactoring.
- `EssentialsCommand` class code refactoring.
- `common.arg.error` message identificator changed to `common.arg.disabled`.
- `kotlinx-serialization` dependency updated.
- `build.gradle` script and gradle.properties file refactoring.
- Gradle wrapper updated to `5.6.3` from `5.6.1`.

### Fixed

- Nickname mismatch in `sendMsg` methods.
- `success message` when user or server command execution failed.
- Wrong message identifier for `top` command.
- `CommandAliases` error for searching blocked command.
- `FileNotFoundException` when user data folder exist, but data in folder no.

### Removed

- Common Project Essentials code.
- `bypassPermissionLevel` property from `CooldownsConfig`.
- `EssentialsCommands` configuration from `CommandsConfig` class.
- `permissionLevel` from `CommandsConfig` class.
- `argUsePermissionLevel` from `CommandConfig` class.
- `disabledWorldsBypassPermLevel` from `CommandsConfig` class.
- `restrictedBlockByPassPermLevel` from `CommandsConfig` class.
- All cooldown classes from project.
- `/clear` command by essentials mod with `ClearCommand` class.

## [1.14.4-0.1.1 ~~.0~~] - 2019-09-27

### Added

- Reloading `HealCommand` configuration by `/ess reload` command.

### Changed

- Resolved https://github.com/MairwunNx/ProjectEssentials/projects/12#card-26857435
- All commands refactoring.
- `CommandAliases` class moved to `commands.helpers`.
- `FlyCommand` and `GodCommand` moved to `commands.abilities`.
- `Air`, `Feed`, `Heal`, `Suicide` commands moved to `commands.health`.

### Fixed

- `Air` command with target out msg for server.
- `Heal` command with target out msg for server.
- `Feed` command with target out msg for server.
- Formatting issue after **reformat code** option.

## [1.14.4-0.1.0 ~~.0~~] - 2019-09-26

### Added

- `/getpos` command and command aliases.
- `ru-RU` localization for `getpos` command.
- `ru-RU` messages for permission out break, list commands.
- `/more` command and command aliases.
- `/day` command and command aliases.
- `/night` command and command aliases.
- `/midnight` command and command aliases.
- `/noon` command and command aliases.
- `/sunset` command and command aliases.
- `/sunrise` command and command aliases.
- Base command `/time` and command aliases.
- `/suicide` command and command aliases.
- `/rain` command and command aliases.
- `/storm` command and command aliases.
- `/sun` command and command aliases.
- `/repair` command and command aliases.
- Ability to repair all items in inventory.

### Fixed

- Not exists messages for permissions list, break commands.
- Incorrect command settings reloading.

## [1.14.4-0.0.5 ~~.0~~] - 2019-09-22

### Added

- `/air` command base logic and command aliases.
- `/fly` command base logic and command aliases.
- `user-data` configuration and load and save fly state by world.
- Nessages for player when command is blocked.
- Checking `forge` version for compatibility with mod.
- Ability to block fly in worlds and setting up bypass for it.
- `ru-RU` localization for restricting fly in worlds functionality.
- `/god` command base logic and command aliases.
- `CommandBase` abstract class for commands.
- Validating for command aliases string values.
- `/list` command base logic and command aliases.
- `/break` base logic and command aliases. [UNSTABLE!!!] SEE #1

### Changed

- Small `EssentialsCommand` object source code improvements.
- Code refactoring. New user storage system improved and fixed.

## [1.14.4-0.0.4 ~~.0~~] - 2019-09-14

### Added

- `/essentials` with sub command `reload` command.
- `/essentials` with sub command `save` command.
- `/essentials` with sub command `version` command.
- Messages `ONLY_PLAYER_CAN` and `DISABLED_COMMAND_ARG` for logging.
- Property `empty` extension for class `String`.
- Message if `/heal` command argument restricted.
- `common.arg.error` for `en-US` and `ru-RU` localizations.
- Message if `/feed` command argument restricted.
- Ability to configure `max food level` and `max saturation level`.
- Mod constants to `ProjectEssentials.kt` file.

### Changed

- `HealCommand` object source code improved.
- `FeedCommand` object source code improved.
- `TopCommand` object source code improved.
- Now argument type for commands: `/heal`, `/feed` is `ServerPlayerEntity` instead `String`.

### Fixed

- Incorrect restricting arguments for `/heal` command.
- Incorrect restricting arguments for `/feed` command.

### Removed

- `ktlintFormat` and `ktlint` tasks from dependencies `jar` task.

## [1.14.4-0.0.3 ~~.0~~] - 2019-09-12

### Added

- Documentation for some code extensions.
- Link to `update.json` for forge updater in `mods.toml`.
- Loading and saving mod configuration file.
- Ability to configure aliases `top` command.
- Ability to configure aliases and permission level for `feed` command.
- Ability to configure `heal` command aliases.
- Ability to configure permission level for `heal` command.
- Ability to configure `feed` command target `isEnabled`.
- Ability to configure `heal` command target `isEnabled`.
- Cooldowns and cooldowns mod configuration for commands.
- Helpers (contains paths for configuration dir (server and client)).
- `argUsePermissionLevel` for commands: `heal`, `feed`.
- Ability to block any command and command aliases.
- Ability to reload configuration **[need in refactoring]**.

### Changed

- `issueTrackerURL` moved to root of `mods.toml` from `[[mods]]`.

## [1.14.4-0.0.2 ~~.0~~] - 2019-09-06

### Changed

- `ProjectEssentials` object now is class.

### Removed

- `Kottle` from dependencies.

## [1.14.4-0.0.1 ~~.1~~] - 2019-09-05

### Added

- Restriction on the use of some commands by the server.

## [1.14.4-0.0.1 ~~.0~~] - 2019-09-05

### Added

- Initial pre-release of Project Essentials.
