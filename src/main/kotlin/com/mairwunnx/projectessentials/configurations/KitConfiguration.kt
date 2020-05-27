package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.helpers.jsonInstance
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object KitConfiguration : IConfiguration<KitConfigurationModel> {
    private val logger = LogManager.getLogger()
    private var configurationData = KitConfigurationModel()

    override val name = "kit"
    override val version = 1
    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "kits.json"

    override fun load() {
        try {
            val configRaw = File(path).readText()
            configurationData = jsonInstance.parse(
                KitConfigurationModel.serializer(), configRaw
            )
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($path) not found!")
            logger.warn("The default configuration will be used")
        } finally {
//            logger.info("Loaded kits (${take().kits.count()})")
        }
    }

    override fun save() {
        File(path).parentFile.mkdirs()

        logger.info("Saving configuration `${name}`")
        val raw = jsonInstance.stringify(
            KitConfigurationModel.serializer(), configuration
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
