package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.core.extensions.empty
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    var lastWorld: String = String.empty,
    var lastWorldPos: String = String.empty,
    var flyEnabledInWorlds: List<String> = listOf(),
    var godEnabledWorlds: List<String> = listOf()
)
