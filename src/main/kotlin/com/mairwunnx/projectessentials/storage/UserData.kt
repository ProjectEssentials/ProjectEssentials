package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.core.extensions.empty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("LastWorld")
    var lastWorld: String = String.empty,
    @SerialName("LastWorldPos")
    var lastWorldPos: String = String.empty,
    @SerialName("FlyEnabledWorlds")
    var flyEnabledInWorlds: List<String> = listOf(),
    @SerialName("GodEnabledWorlds")
    var godEnabledWorlds: List<String> = listOf()
)
