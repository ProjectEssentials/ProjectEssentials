package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.COMMANDS_CONFIG
import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object ModConfiguration {
    private val logger = LogManager.getLogger()
    private var commandsConfig = CommandsConfig()

    fun loadConfig() {
        logger.info("Loading commands configuration")

        try {
            val configRaw = File(COMMANDS_CONFIG).readText()
            commandsConfig = jsonInstance.parse(CommandsConfig.serializer(), configRaw)
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COMMANDS_CONFIG) not found!")
            logger.warn("The default configuration will be used")
        }
    }

    fun saveConfig() {
        logger.info("Saving commands configuration")
        File(MOD_CONFIG_FOLDER).mkdirs()

        val commandsConfigRaw = jsonInstance.stringify(
            CommandsConfig.serializer(), commandsConfig
        )

        try {
            File(COMMANDS_CONFIG).writeText(commandsConfigRaw)
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving commands configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig
}
