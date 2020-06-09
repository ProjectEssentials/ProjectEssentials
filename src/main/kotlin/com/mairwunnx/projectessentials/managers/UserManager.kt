package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.userDataConfiguration

object UserManager {
    fun getUsers() = userDataConfiguration.users.asSequence()
    fun getUserByName(name: String) = getUsers().find { it.name == name }
    fun getUserByUUID(uuid: String) = getUsers().find { it.uuid == uuid }
    fun getUserByNameOrUUID(name: String, uuid: String) = getUsers().find {
        it.name == name || it.uuid == uuid
    }
}
