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
        val fly: Fly = Fly()
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
            val aliases: List<String> = listOf("eheal")
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
            val aliases: List<String> = listOf(
                "eat", "eeat", "efeed"
            )
        )

        @Serializable
        data class Top(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etop")
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
            val aliases: List<String> = listOf("eair")
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
            val aliases: List<String> = listOf("efly"),
            @SerialName("AutoFlyEnabled")
            val autoFlyEnabled: Boolean = true,
            @SerialName("FlyDisabledWorlds")
            val flyDisabledWorlds: List<String> = listOf(),
            @SerialName("DisabledWorldsBypassPermLevel")
            val disabledWorldsBypassPermLevel: Int = 3
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
