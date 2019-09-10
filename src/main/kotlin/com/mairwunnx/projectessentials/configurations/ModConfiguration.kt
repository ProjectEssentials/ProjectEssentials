package com.mairwunnx.projectessentials.configurations

import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ModConfiguration {
    private val logger: Logger = LogManager.getLogger()
    private val clientMinecraftDir by lazy { Minecraft.getInstance().gameDir.absolutePath }
    private val serverMinecraftDir by lazy { File(".").absolutePath }
    private var configDir: String = getPathBySide()
    private val commandsConfigFilePath by lazy { configDir + File.separator + "commands.json" }
    private val cooldownsConfigFilePath by lazy { configDir + File.separator + "cooldowns.json" }
    private var commandsConfig: CommandsConfig = CommandsConfig()
    private var cooldownsCofig: CooldownsConfig = CooldownsConfig()

    private fun getPathBySide(): String {
        var path = ""
        val clientConfigPaths = Runnable {
            path = clientMinecraftDir +
                    File.separator +
                    "config" +
                    File.separator +
                    "ProjectEssentials"
        }
        val serverConfigPaths = Runnable {
            path = serverMinecraftDir +
                    File.separator +
                    "config" +
                    File.separator +
                    "ProjectEssentials"
        }
        DistExecutor.runWhenOn(Dist.CLIENT) { clientConfigPaths }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) { serverConfigPaths }
        return path
    }

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
                File(commandsConfigFilePath).readText(StandardCharsets.UTF_8)
            val elapsedTime = measureTimeMillis {
                commandsConfig = Json.parse(CommandsConfig.serializer(), configRaw)
            }
            logger.info("Loading commands configuration done with ${elapsedTime}ms")
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($commandsConfigFilePath) not found")
            logger.warn("The default configuration will be used")
        }
    }

    @UseExperimental(UnstableDefault::class)
    private fun loadCooldownsConfig() {
        try {
            val configRaw: String =
                File(cooldownsConfigFilePath).readText(StandardCharsets.UTF_8)
            val elapsedTime = measureTimeMillis {
                cooldownsCofig = Json.parse(CooldownsConfig.serializer(), configRaw)
            }
            logger.info("Loading cooldowns configuration done with ${elapsedTime}ms")
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($cooldownsConfigFilePath) not found")
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
        createConfigDirs()
        saveCommandsConfig(json)
        saveCooldownsConfig(json)
    }

    private fun createConfigDirs() {
        logger.info("Creating config directories for configurations")
        val configDirectory = File(configDir)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }

    private fun saveCommandsConfig(json: Json) {
        val commandsConfigRaw = json.stringify(
            CommandsConfig.serializer(),
            commandsConfig
        )

        try {
            File(commandsConfigFilePath).writeText(commandsConfigRaw)
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
            File(cooldownsConfigFilePath).writeText(cooldownsConfigRaw)
            logger.info("Saving cooldowns configuration done")
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving cooldowns configuration", ex)
        }
    }

    fun getCommandsConfig() = commandsConfig

    fun getCooldownsConfig() = cooldownsCofig
}
