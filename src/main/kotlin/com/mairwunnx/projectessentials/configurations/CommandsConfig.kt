package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommandsConfig(
    @SerialName("Commands")
    val commands: Commands = Commands()
) {
    @Serializable
    data class Commands(
        @SerialName("Heal")
        val heal: Heal = Heal(),
        @SerialName("Feed")
        val feed: Feed = Feed(),
        @SerialName("Top")
        val top: Top = Top()
    ) {
        @Serializable
        data class Heal(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("CommandAliases")
            val aliases: List<String> = listOf("eheal")
        )

        @Serializable
        data class Feed(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("MaxFoodSaturationLevel")
            val maxFoodSaturationLevel: Float = 5.0f,
            @SerialName("MaxFoodLevel")
            val maxFoodLevel: Int = 20,
            @SerialName("CommandAliases")
            val aliases: List<String> = listOf(
                "eat", "eeat", "efeed"
            )
        )

        @Serializable
        data class Top(
            @SerialName("PermissionLevel")
            val permissionLevel: Int = 2,
            @SerialName("CommandAliases")
            val aliases: List<String> = listOf("etop")
        )
    }
}
