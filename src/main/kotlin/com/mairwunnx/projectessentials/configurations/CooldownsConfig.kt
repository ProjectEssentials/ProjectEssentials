package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CooldownsConfig(
    @SerialName("BypassPermissionLevel")
    val bypassPermissionLevel: Int = 2,
    @SerialName("CommandCooldowns")
    val commandCooldowns: MutableList<String> = mutableListOf(),
    @SerialName("IgnoredPlayers")
    val ignoredPlayers: MutableList<String> = mutableListOf()
)
