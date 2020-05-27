package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.Serializable

@Serializable
data class UserDataConfigurationModel(
    val purgeDaysDelay: Int = 7,
    val users: MutableList<User> = mutableListOf()
) {
    @Serializable
    data class User(
        var name: String,
        var uuid: String,
        var lastDateTime: String,
        var lastWorldName: String,
        var lastDimension: String,
        var lastPosition: String,
        var lastIPAddress: String,
        var flyWorldDimensions: MutableList<String> = mutableListOf(),
        var godWorldDimensions: MutableList<String> = mutableListOf()
    )
}
