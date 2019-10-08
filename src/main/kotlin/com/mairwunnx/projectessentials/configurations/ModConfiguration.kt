package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.COMMANDS_CONFIG
import com.mairwunnx.projectessentials.COOLDOWNS_CONFIG
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
    private var cooldownsConfig = CooldownsConfig()

    fun loadConfig() {
        logger.info("    - loading base modification configuration ...")
        loadCommandsConfig()
        loadCooldownsConfig()
    }

    private fun loadCommandsConfig() {
        try {
            logger.info("        - loading commands configuration ...")
            val configRaw = File(COMMANDS_CONFIG).readText()
            commandsConfig = Json.parse(CommandsConfig.serializer(), configRaw)
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COMMANDS_CONFIG) not found!")
            logger.warn("The default configuration will be used.")
        }
    }

    private fun loadCooldownsConfig() {
        try {
            logger.info("        - loading cooldowns configuration ...")
            val configRaw = File(COOLDOWNS_CONFIG).readText()
            cooldownsConfig = Json.parse(CooldownsConfig.serializer(), configRaw)
            logger.info("    - loaded cooldowns (${cooldownsConfig.commandCooldowns.size})")
            cooldownsConfig.commandCooldowns.forEach {
                val command = it.split("=")[0]
                val cooldown = it.split("=")[1]
                logger.info("        - command: ${command}; cooldown: $cooldown")
            }
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COOLDOWNS_CONFIG) not found!")
            logger.warn("The default configuration will be used.")
        }
    }

    fun saveConfig() {
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
        createConfigDirs()
        saveCommandsConfig(json)
        saveCooldownsConfig(json)
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

    private fun saveCooldownsConfig(json: Json) {
        logger.info("        - saving cooldowns configuration ...")
        val cooldownsConfigRaw = json.stringify(
            CooldownsConfig.serializer(),
            cooldownsConfig
        )

        try {
            File(COOLDOWNS_CONFIG).writeText(cooldownsConfigRaw)
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving cooldowns configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig
    fun getCooldownsConfig() = cooldownsConfig
}
