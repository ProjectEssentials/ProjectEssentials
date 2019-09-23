package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommandsConfig(
    @SerialName("Commands")
    val commands: Commands = Commands(),
    @SerialName("EssentialsCommands")
    val essentialsCommands: EssentialsCommands = EssentialsCommands(),
    @SerialName("DisabledCommands")
    val disabledCommands: List<String> = listOf()
) {
    @Serializable
    data class Commands(
        @SerialName("Heal")
        val heal: Heal = Heal(),
        @SerialName("Feed")
        val feed: Feed = Feed(),
        @SerialName("Top")
        val top: Top = Top(),
        @SerialName("Air")
        val air: Air = Air(),
        @SerialName("Fly")
        val fly: Fly = Fly(),
        @SerialName("God")
        val god: God = God(),
        @SerialName("List")
        val list: List = List(),
        @SerialName("Break")
        val `break`: Break = Break(),
        @SerialName("GetPos")
        val getPos: GetPos = GetPos(),
        @SerialName("More")
        val more: More = More(),
        @SerialName("Day")
        val day: Day = Day(),
        @SerialName("Night")
        val night: Night = Night(),
        @SerialName("MidNight")
        val midNight: MidNight = MidNight(),
        @SerialName("Noon")
        val noon: Noon = Noon(),
        @SerialName("Sunset")
        val sunset: Sunset = Sunset(),
        @SerialName("Sunrise")
        val sunrise: Sunrise = Sunrise()
    ) {
        @Serializable
        data class Heal(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eheal")
        )

        @Serializable
        data class Feed(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("MaxFoodSaturationLevel")
            val maxFoodSaturationLevel: Float = 5.0f,
            @SerialName("MaxFoodLevel")
            val maxFoodLevel: Int = 20,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "eat", "eeat", "efeed"
            )
        )

        @Serializable
        data class Top(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("etop")
        )

        @Serializable
        data class Air(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eair")
        )

        @Serializable
        data class Fly(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("efly"),
            @SerialName("AutoFlyEnabled")
            val autoFlyEnabled: Boolean = true,
            @SerialName("FlyDisabledWorlds")
            val flyDisabledWorlds: kotlin.collections.List<String> = listOf(),
            @SerialName("DisabledWorldsBypassPermLevel")
            val disabledWorldsBypassPermLevel: Int = 3
        )

        @Serializable
        data class God(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("egod", "tgm"),
            @SerialName("AutoGodModeEnabled")
            val autoGodModeEnabled: Boolean = true,
            @SerialName("GodModeDisabledWorlds")
            val godModeDisabledWorlds: kotlin.collections.List<String> = listOf(),
            @SerialName("DisabledWorldsBypassPermLevel")
            val disabledWorldsBypassPermLevel: Int = 3
        )

        @Serializable
        data class List(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("MaxDisplayedPlayers")
            val maxDisplayedPlayers: Int = 16,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "elist",
                "online",
                "eonline",
                "playerlist",
                "eplayerlist",
                "plist",
                "eplist",
                "who",
                "ewho"
            )
        )

        @Serializable
        data class Break(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("RestrictedBlockByPassPermLevel")
            val restrictedBlockByPassPermLevel: Int = 3,
            @SerialName("RestrictedBlocks")
            val restrictedBlocks: kotlin.collections.List<String> = listOf("minecraft:bedrock"),
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("ebreak")
        )

        @Serializable
        data class GetPos(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("ArgUsePermissionLevel")
            val argUsePermissionLevel: Int = 3,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "eposition",
                "mypos"
            )
        )

        @Serializable
        data class More(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 3,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "emore",
                "dupe"
            )
        )

        @Serializable
        data class Day(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "eday"
            )
        )

        @Serializable
        data class Night(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "enight"
            )
        )

        @Serializable
        data class MidNight(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "emidnight"
            )
        )

        @Serializable
        data class Noon(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "enoon", "midday", "noonday"
            )
        )

        @Serializable
        data class Sunset(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "esunset", "dusk", "sundown", "evening"
            )
        )

        @Serializable
        data class Sunrise(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "esunrise", "dawn", "morning", "morn"
            )
        )
    }

    @Serializable
    data class EssentialsCommands(
        @SerialName("VersionPermissionLevel")
        val versionPermissionLevel: Int = 0,
        @SerialName("ReloadPermissionLevel")
        val reloadPermissionLevel: Int = 4,
        @SerialName("SavePermissionLevel")
        val savePermissionLevel: Int = 4
    )
}
