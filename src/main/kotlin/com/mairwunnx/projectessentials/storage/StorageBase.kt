package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.helpers.USER_DATA_FOLDER
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import kotlin.system.measureTimeMillis

object StorageBase {
    private val logger: Logger = LogManager.getLogger()
    private val userData = hashMapOf<String, UserData>()

    fun getData(uuid: String): UserData {
        if (userData.containsKey(uuid)) {
            return userData[uuid] ?: UserData()
        }
        return UserData()
    }

    fun setData(uuid: String, data: UserData) {
        userData[uuid] = data
    }

    @UnstableDefault
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun loadUserData() {
        logger.info("Starting loading user-data for ProjectEssentials mod ...")
        val configDirectory = File(USER_DATA_FOLDER)
        if (!configDirectory.exists()) configDirectory.mkdirs()
        val users = File(USER_DATA_FOLDER).list().filter {
            if (File(it).isFile) return@filter false
            return@filter true
        }

        val elapsedTime = measureTimeMillis {
            users.forEach {
                val userId = it
                val userDataRaw = File(
                    USER_DATA_FOLDER + File.separator + it + File.separator + "data.json"
                ).readText()
                val userDataClass = Json.parse(UserData.serializer(), userDataRaw)
                userData[userId] = userDataClass
            }
        }
        logger.info("Loading user data done with ${elapsedTime}ms")
    }

    @UnstableDefault
    fun saveUserData() {
        createConfigDirs(USER_DATA_FOLDER)
        userData.keys.forEach {
            val userId = it
            val userDataClass = userData[userId]!!
            val dataFolder = USER_DATA_FOLDER + File.separator + userId
            createConfigDirs(dataFolder)
            val dataPath = dataFolder + File.separator + "data.json"
            val json = Json(
                JsonConfiguration(
                    encodeDefaults = true,
                    strictMode = true,
                    unquoted = false,
                    allowStructuredMapKeys = true,
                    prettyPrint = true,
                    useArrayPolymorphism = false
                )
            )
            val userDataRaw = json.stringify(UserData.serializer(), userDataClass)

            try {
                File(dataPath).writeText(userDataRaw)
            } catch (ex: SecurityException) {
                logger.error("An error occurred while saving commands configuration", ex)
            }
        }
    }

    private fun createConfigDirs(path: String) {
        logger.info("Creating config directories for user data ($path)")
        val configDirectory = File(path)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }
}
