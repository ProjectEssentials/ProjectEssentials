package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CooldownsConfig(
    @SerialName("CommandCooldowns")
    val commandCooldowns: MutableList<String> = mutableListOf(),
    @SerialName("CooldownIgnoredPlayers")
    val cooldownIgnoredPlayers: MutableList<String> = mutableListOf(),
    @SerialName("CooldownBypassPermissionLevel")
    val cooldownBypassPermissionLevel: Int = 2
)
