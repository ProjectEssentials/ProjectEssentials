package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.helpers.COMMANDS_CONFIG
import com.mairwunnx.projectessentials.helpers.COOLDOWNS_CONFIG
import com.mairwunnx.projectessentials.helpers.MOD_CONFIG_FOLDER
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ModConfiguration {
    private val logger: Logger = LogManager.getLogger()
    private var commandsConfig: CommandsConfig = CommandsConfig()
    private var cooldownsCofig: CooldownsConfig = CooldownsConfig()

    @UseExperimental(UnstableDefault::class)
    fun loadConfig() {
        logger.info("Starting loading settings for ProjectEssentials mod ...")
        loadCommandsConfig()
        loadCooldownsConfig()
    }

    @UseExperimental(UnstableDefault::class)
    private fun loadCommandsConfig() {
        try {
            val configRaw: String =
                File(COMMANDS_CONFIG).readText(StandardCharsets.UTF_8)
            val elapsedTime = measureTimeMillis {
                commandsConfig = Json.parse(CommandsConfig.serializer(), configRaw)
            }
            logger.info("Loading commands configuration done with ${elapsedTime}ms")
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COMMANDS_CONFIG) not found")
            logger.warn("The default configuration will be used")
        }
    }

    @UseExperimental(UnstableDefault::class)
    private fun loadCooldownsConfig() {
        try {
            val configRaw: String =
                File(COOLDOWNS_CONFIG).readText(StandardCharsets.UTF_8)
            val elapsedTime = measureTimeMillis {
                cooldownsCofig = Json.parse(CooldownsConfig.serializer(), configRaw)
            }
            logger.info("Loading cooldowns configuration done with ${elapsedTime}ms")
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COOLDOWNS_CONFIG) not found")
            logger.warn("The default configuration will be used")
        }
    }

    @UseExperimental(UnstableDefault::class)
    fun saveConfig() {
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
        logger.info("Creating config directories for configurations")
        val configDirectory = File(MOD_CONFIG_FOLDER)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }

    private fun saveCommandsConfig(json: Json) {
        val commandsConfigRaw = json.stringify(
            CommandsConfig.serializer(),
            commandsConfig
        )

        try {
            File(COMMANDS_CONFIG).writeText(commandsConfigRaw)
            logger.info("Saving commands configuration done")
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving commands configuration", ex)
        }
    }

    private fun saveCooldownsConfig(json: Json) {
        val cooldownsConfigRaw = json.stringify(
            CooldownsConfig.serializer(),
            cooldownsCofig
        )

        try {
            File(COOLDOWNS_CONFIG).writeText(cooldownsConfigRaw)
            logger.info("Saving cooldowns configuration done")
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving cooldowns configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig
    fun getCooldownsConfig() = cooldownsCofig
}
