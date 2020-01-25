# Configuration

## $ User data configuraton

### Configuration location

```none
.minecraft/config/ProjectEssentials/user-data/{uuid}/data.json
```

### Configuration description

It configuration file store data about player, e.g last position, fly state and other

#### Default configuration

```json
{
    "lastWorld": "",
    "lastWorldPos": "",
    "flyEnabledInWorlds": [],
    "godEnabledWorlds": []
}
```

#### Small documentation :)

| Property               | Type             | Description      |
|---                     |---               |---               |
|`lastWorld`             |`String`          |Last world *(using full name, e.g `New World&minecraft:overworld`)*. Symbol `&` uses as split char.|
|`lastWorldPos`          |`String`          |Last player position in world *(includes only x, y, z axis)*, e.g `-380, 65, -1139`|
|`flyEnabledInWorlds`    |`List<String>`    |Contains world names in which enabled fly mode *(basically needed for automatically fly enabling)*. e.g `New World`.|
|`godEnabledWorlds`      |`List<String>`    |Contains world names in which enabled god mode *(basically needed for automatically god enabling)*. e.g `New World`.|

## $ Commands configuration

### Configuration location

```none
.minecraft/config/ProjectEssentials/commands.json
```

### Configuration description

It configuration file controlling command aliases and some options for some command

#### Default configuration

```json
{
    "Commands": {
        "Heal": {
            "EnableArgs": true,
            "Aliases": ["eheal"]
        },
        "Feed": {
            "EnableArgs": true,
            "MaxFoodSaturationLevel": 5.0,
            "MaxFoodLevel": 20,
            "Aliases": ["eat", "eeat", "efeed"]
        },
        "Top": {
            "Aliases": ["etop"]
        },
        "Air": {
            "EnableArgs": true,
            "Aliases": ["eair"]
        },
        "Fly": {
            "EnableArgs": true,
            "Aliases": ["efly"],
            "AutoFlyEnabled": true,
            "FlyDisabledWorlds": []
        },
        "God": {
            "EnableArgs": true,
            "Aliases": ["egod","tgm"],
            "AutoGodModeEnabled": true,
            "GodModeDisabledWorlds": []
        },
        "List": {
            "MaxDisplayedPlayers": 16,
            "Aliases": [
                "elist",
                "online",
                "eonline",
                "playerlist",
                "eplayerlist",
                "plist",
                "eplist",
                "who",
                "ewho"
            ]
        },
        "Break": {
            "RestrictedBlocks": ["minecraft:bedrock"],
            "Aliases": ["ebreak"]
        },
        "GetPos": {
            "EnableArgs": true,
            "Aliases": ["eposition", "mypos"]
        },
        "SendPos": {
            "Aliases": ["esendpos"]
        },
        "More": {
            "Aliases": ["emore", "dupe"]
        },
        "Day": {
            "Aliases": ["eday"]
        },
        "Night": {
            "Aliases": ["enight"]
        },
        "MidNight": {
            "Aliases": ["emidnight"]
        },
        "Noon": {
            "Aliases": ["enoon", "midday", "noonday"]
        },
        "Sunset": {
            "Aliases": ["esunset", "dusk", "sundown", "evening"]
        },
        "Sunrise": {
            "Aliases": ["esunrise", "dawn", "morning", "morn"]
        },
        "Time": {
            "Aliases": ["etime"]
        },
        "Suicide": {
            "Aliases": ["esuicide"]
        },
        "Rain": {
            "DefaultDuration": 13000,
            "Aliases": ["erain"]
        },
        "Storm": {
            "DefaultDuration": 13000,
            "Aliases": [
                "estorm", "thunder", "ethunder", "goodweather"
            ]
        },
        "Sun": {
            "Aliases": [
                "esun", "weatherclear", "clearsky", "sky", "esky"
            ]
        },
        "Repair": {
            "Aliases": ["fix", "efix", "erepair"]
        },
        "Ping": {
            "Aliases": ["eping"]
        },
        "Afk": {
            "Aliases": ["afk", "eafk", "away", "eaway"]
        },
        "Burn": {
            "DefaultDuration": 10,
            "Aliases": ["burn","eburn"]
        },
        "Lightning": {
            "Aliases": [
                "lightning", "elightning", "shock", "eshock", "thor", "ethor"
            ]
        },
        "TpPos": {
            "Aliases": ["etppos"]
        },
        "TpAll": {
            "Aliases": ["etpall"]
        },
        "TpHere": {
            "Aliases": ["etphere", "s"]
        },
        "Tpa": {
            "Aliases": ["etpa", "call", "ecall"],
            "TimeOut": 45
        },
        "TpaAll": {
            "Aliases": ["etpaall", "callall", "ecallall"]
        },
        "TpaHere": {
            "Aliases": ["etpahere", "callhere", "ecallhere"]
        },
        "TpAccept": {
            "Aliases": ["etpaccept", "tpyes", "etpyes"]
        },
        "TpDeny": {
            "Aliases": ["etpdeny", "tpno", "etpno"]
        },
        "TpToggle": {
            "Aliases": ["etptoggle", "tpoff", "etpoff"]
        },
        "TpaCancel": {
            "Aliases": ["etpacancel"]
        }
    },
    "DisabledCommands": []
}
```

#### Common properties

| Property                 | Type             | Description                                               |
|---                       |---               |---                                                        |
|`Commands`                |`List<Command>`   |Stores all commands.                                       |
|`Heal`, `Feed` and other  |`Command`         |Just command with some options.                            |
|`EnableArgs`              |`Boolean`         |If value true then arguments for command will be enabled.  |
|`Aliases`                 |`List<String>`    |Contain all command aliases.                               |

#### Specific properties

| Property                         | Type             | Description    |
|---                               |---               |---             |
|`Feed.MaxFoodSaturationLevel`     |`Float`           |The saturation level that will be obtained when the command is executed.  |
|`Feed.MaxFoodLevel`               |`Int`             |The food level that will be obtained when the command is executed.  |
|`Fly.AutoFlyEnabled`              |`Boolean`         |If value true then auto fly in world or server will be enabled.  |
|`Fly.FlyDisabledWorlds`           |`List<String>`    |World names in which disabled fly mode.  |
|`God.AutoGodModeEnabled`          |`Boolean`         |If value true then auto god mode in world or server will be enabled.  |
|`God.GodModeDisabledWorlds`       |`List<String>`    |World names in which disabled god mode.  |
|`Break.RestrictedBlocks`          |`List<String>`    |Contains all restricted blocks for breaking by command.  |
|`Rain\Storm.DefaultDuration`      |`Int`             |Default duration for weather event. (probably in minecraft ticks `¯\_(ツ)_/¯`)  |
|`Burn.DefaultDuration`            |`Int`             |Default duration player firing. (also probably in minecraft ticks `¯\_(ツ)_/¯`)  |
|`Tpa.TimeOut`                     |`Int`             |Time out for teleport request in seconds.  |

## If you have any questions or encounter a problem, be sure to open an [issue](https://github.com/ProjectEssentials/ProjectEssentials/issues/new/choose)
