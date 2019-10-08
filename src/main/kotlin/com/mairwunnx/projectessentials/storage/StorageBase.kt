package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.USER_DATA_FOLDER
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.system.measureTimeMillis

@UseExperimental(UnstableDefault::class)
object StorageBase {
    private val logger = LogManager.getLogger()
    private val userData = hashMapOf<String, UserData>()

    fun getData(uuid: String): UserData {
        if (userData.containsKey(uuid)) {
            val requestedData = userData[uuid] ?: UserData()
            logger.debug("Requested data ($requestedData) for UUID ($uuid).")
            return requestedData
        }
        logger.debug("Requested data not found for UUID ($uuid), will be used default data.")
        return UserData()
    }

    fun setData(uuid: String, data: UserData) {
        userData[uuid] = data
        logger.debug("Installed data (${data}) for UUID ($uuid).")
    }

    fun loadUserData() {
        logger.info("    - loading user data configurations ...")

        createConfigDirs(USER_DATA_FOLDER)
        val users = File(USER_DATA_FOLDER).list()?.filter {
            if (File(it).isFile) return@filter false
            return@filter true
        }

        val elapsedTime = measureTimeMillis {
            users?.forEach {
                logger.debug("        - processing $it user data ...")
                val userId = it
                val userDataRaw = File(
                    USER_DATA_FOLDER + File.separator + it + File.separator + "data.json"
                ).readText()
                val userDataClass = Json.parse(UserData.serializer(), userDataRaw)
                userData[userId] = userDataClass
            }
        }
        logger.info("Loading user data done configurations with ${elapsedTime}ms")
    }

    fun saveUserData() {
        createConfigDirs(USER_DATA_FOLDER)
        userData.keys.forEach {
            logger.debug("        - processing $it user data ...")

            val userId = it
            val userDataClass = userData[userId]!!
            val dataFolder = USER_DATA_FOLDER + File.separator + userId
            val dataPath = dataFolder + File.separator + "data.json"

            createConfigDirs(dataFolder)
            logger.debug("        - setup json configuration for parsing ...")
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
        logger.info("        - creating config directory for user data ($path)")
        val configDirectory = File(path)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }
}
