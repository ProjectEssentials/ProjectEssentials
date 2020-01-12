package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.COMMANDS_CONFIG
import com.mairwunnx.projectessentialscore.helpers.MOD_CONFIG_FOLDER
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

@UseExperimental(UnstableDefault::class)
object ModConfiguration {
    private val logger = LogManager.getLogger()
    private var commandsConfig = CommandsConfig()

    fun loadConfig() {
        logger.info("    - loading base modification configuration ...")
        loadCommandsConfig()
    }

    private fun loadCommandsConfig() {
        try {
            logger.info("        - loading commands configuration ...")
            val configRaw = File(COMMANDS_CONFIG).readText()
            val json = Json(
                JsonConfiguration(
                    encodeDefaults = true,
                    strictMode = false,
                    unquoted = false,
                    allowStructuredMapKeys = true,
                    prettyPrint = true
                )
            )
            commandsConfig = json.parse(CommandsConfig.serializer(), configRaw)
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COMMANDS_CONFIG) not found!")
            logger.warn("The default configuration will be used.")
        }
    }

    fun saveConfig() {
        logger.debug("        - setup json configuration for parsing ...")
        val json = Json(
            JsonConfiguration(
                encodeDefaults = true,
                strictMode = false,
                unquoted = false,
                allowStructuredMapKeys = true,
                prettyPrint = true,
                useArrayPolymorphism = false
            )
        )
        createConfigDirs()
        saveCommandsConfig(json)
    }

    private fun createConfigDirs() {
        logger.info("        - creating directories for configurations ...")
        val configDirectory = File(MOD_CONFIG_FOLDER)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }

    private fun saveCommandsConfig(json: Json) {
        logger.info("        - saving commands configuration ...")
        val commandsConfigRaw = json.stringify(
            CommandsConfig.serializer(),
            commandsConfig
        )

        try {
            File(COMMANDS_CONFIG).writeText(commandsConfigRaw)
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving commands configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig
}
