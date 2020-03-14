# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

## [1.15.2-1.0.0] - 2020-02-08

### Added

- Initial release.
