package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.USER_DATA_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.measureTimeMillis

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
        logger.info("Loading user data configurations")

        File(USER_DATA_FOLDER).mkdirs()
        val users = File(USER_DATA_FOLDER).list()?.filter {
            if (File(it).isFile) return@filter false
            return@filter true
        }

        val elapsedTime = measureTimeMillis {
            users?.forEach {
                try {
                    val userId = it
                    val userDataRaw = File(
                        USER_DATA_FOLDER + File.separator + it + File.separator + "data.json"
                    ).readText()
                    val userDataClass = jsonInstance.parse(UserData.serializer(), userDataRaw)
                    userData[userId] = userDataClass
                } catch (_: FileNotFoundException) {
                    logger.info("Loading user data for $it skipped! not found!")
                }
            }
        }
        logger.info("Loading user data done configurations with ${elapsedTime}ms")
    }

    fun saveUserData() {
        File(USER_DATA_FOLDER).mkdirs()
        userData.keys.forEach {

            val userId = it
            val userDataClass = userData[userId]!!
            val dataFolder = USER_DATA_FOLDER + File.separator + userId
            val dataPath = dataFolder + File.separator + "data.json"

            File(dataFolder).mkdirs()
            val userDataRaw = jsonInstance.stringify(UserData.serializer(), userDataClass)
            try {
                File(dataPath).writeText(userDataRaw)
            } catch (ex: SecurityException) {
                logger.error("An error occurred while saving user configuration", ex)
            }
        }
    }
}
