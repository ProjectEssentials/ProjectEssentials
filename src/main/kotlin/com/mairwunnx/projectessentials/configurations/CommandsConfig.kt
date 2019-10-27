package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommandsConfig(
    @SerialName("Commands")
    val commands: Commands = Commands(),
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
        val sunrise: Sunrise = Sunrise(),
        @SerialName("Time")
        val time: Time = Time(),
        @SerialName("Suicide")
        val suicide: Suicide = Suicide(),
        @SerialName("Rain")
        val rain: Rain = Rain(),
        @SerialName("Storm")
        val storm: Storm = Storm(),
        @SerialName("Sun")
        val sun: Sun = Sun(),
        @SerialName("Repair")
        val repair: Repair = Repair(),
        @SerialName("Ping")
        val ping: Ping = Ping(),
        @SerialName("Afk")
        val afk: Afk = Afk(),
        @SerialName("Burn")
        val burn: Burn = Burn()
    ) {
        @Serializable
        data class Heal(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eheal")
        )

        @Serializable
        data class Feed(
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
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("etop")
        )

        @Serializable
        data class Air(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eair")
        )

        @Serializable
        data class Fly(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("efly"),
            @SerialName("AutoFlyEnabled")
            val autoFlyEnabled: Boolean = true,
            @SerialName("FlyDisabledWorlds")
            val flyDisabledWorlds: kotlin.collections.List<String> = listOf()
        )

        @Serializable
        data class God(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("egod", "tgm"),
            @SerialName("AutoGodModeEnabled")
            val autoGodModeEnabled: Boolean = true,
            @SerialName("GodModeDisabledWorlds")
            val godModeDisabledWorlds: kotlin.collections.List<String> = listOf()
        )

        @Serializable
        data class List(
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
            @SerialName("RestrictedBlocks")
            val restrictedBlocks: kotlin.collections.List<String> = listOf("minecraft:bedrock"),
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("ebreak")
        )

        @Serializable
        data class GetPos(
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
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("emore", "dupe")
        )

        @Serializable
        data class Day(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eday")
        )

        @Serializable
        data class Night(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("enight")
        )

        @Serializable
        data class MidNight(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("emidnight")
        )

        @Serializable
        data class Noon(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "enoon", "midday", "noonday"
            )
        )

        @Serializable
        data class Sunset(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "esunset", "dusk", "sundown", "evening"
            )
        )

        @Serializable
        data class Sunrise(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "esunrise", "dawn", "morning", "morn"
            )
        )

        @Serializable
        data class Time(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("etime")
        )

        @Serializable
        data class Suicide(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("esuicide")
        )

        @Serializable
        data class Rain(
            @SerialName("DefaultDuration")
            val defaultDuration: Int = 13000,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("erain")
        )

        @Serializable
        data class Storm(
            @SerialName("DefaultDuration")
            val defaultDuration: Int = 13000,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "estorm", "thunder", "ethunder", "goodweather"
            )
        )

        @Serializable
        data class Sun(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "esun", "weatherclear", "clearsky", "sky", "esky"
            )
        )

        @Serializable
        data class Repair(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "fix", "efix", "erepair"
            )
        )

        @Serializable
        data class Ping(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf("eping")
        )

        @Serializable
        data class Afk(
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "afk", "eafk", "away", "eaway"
            )
        )

        @Serializable
        data class Burn(
            @SerialName("DefaultDuration")
            val defaultDuration: Int = 10,
            @SerialName("Aliases")
            val aliases: kotlin.collections.List<String> = listOf(
                "burn", "eburn"
            )
        )
    }
}
