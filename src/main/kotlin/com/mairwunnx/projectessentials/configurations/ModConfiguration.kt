package com.mairwunnx.projectessentials.configurations

import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ModConfiguration {
    private val logger: Logger = LogManager.getLogger()
    private val minecraftDir = Minecraft.getInstance().gameDir.absolutePath
    private val configDir =
        minecraftDir + File.separator + "config" + File.separator + "ProjectEssentials"
    private val commandsConfigFilePath = configDir + File.separator + "commands.json"
    private var commandsConfig: CommandsConfig = CommandsConfig()

    @UseExperimental(UnstableDefault::class)
    fun loadConfig() {
        logger.info("Starting loading settings for ProjectEssentials mod ...")

        try {
            val commandsConfigRaw: String =
                File(commandsConfigFilePath).readText(StandardCharsets.UTF_8)
            val elapsedTime = measureTimeMillis {
                commandsConfig = Json.parse(CommandsConfig.serializer(), commandsConfigRaw)
            }
            logger.info("Loading configuration done with ${elapsedTime}ms")
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($commandsConfigFilePath) not found")
            logger.warn("The default configuration will be used")
        }
    }

    @UseExperimental(UnstableDefault::class)
    fun saveConfig() {
        logger.info("Saving Project Essentials configuration ...")

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
        val commandsConfigRaw = json.stringify(
            CommandsConfig.serializer(),
            commandsConfig
        )

        try {
            val configDirectory = File(configDir)
            if (!configDirectory.exists()) configDirectory.mkdirs()
            File(commandsConfigFilePath).writeText(commandsConfigRaw)
            logger.info("Saving Project Essentials configuration done")
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig
}
