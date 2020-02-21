# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
