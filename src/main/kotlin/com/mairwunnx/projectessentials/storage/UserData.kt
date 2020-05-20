package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    var lastWorld: String = String.empty,
    var lastPosition: String = String.empty,
    var flyWorlds: List<String> = listOf(),
    var godWorlds: List<String> = listOf()
)
