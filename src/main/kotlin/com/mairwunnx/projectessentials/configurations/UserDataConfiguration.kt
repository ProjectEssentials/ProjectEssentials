package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.helpers.jsonInstance
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException
import java.time.Duration
import java.time.ZonedDateTime

object UserDataConfiguration : IConfiguration<UserDataConfigurationModel> {
    private val logger = LogManager.getLogger()
    private var configurationData = UserDataConfigurationModel()

    override val name = "user-data"
    override val version = 1
    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "user-data.json"

    override fun load() {
        try {
            val configRaw = File(path).readText()
            configurationData = jsonInstance.parse(
                UserDataConfigurationModel.serializer(), configRaw
            )
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($path) not found!")
            logger.warn("The default configuration will be used")
        } finally {
            logger.info("Loaded local user data (${take().users.count()})")
            logger.debug("Purging data of old users for ${take().purgeDaysDelay} days").also {
                take().users.removeIf {
                    Duration.between(
                        ZonedDateTime.parse(it.lastDateTime), ZonedDateTime.now()
                    ).toDays() >= take().purgeDaysDelay
                }
            }
        }
    }

    override fun save() {
        File(path).parentFile.mkdirs()

        logger.info("Saving configuration `${name}`")
        val raw = jsonInstance.stringify(
            UserDataConfigurationModel.serializer(), configuration
        )
        try {
            File(path).writeText(raw)
        } catch (ex: SecurityException) {
            logger.error(
                "An error occurred while saving $name configuration", ex
            )
        }
    }

    override fun take() = configurationData
}
