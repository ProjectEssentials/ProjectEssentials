@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.*
import com.mairwunnx.projectessentials.commands.time.*
import com.mairwunnx.projectessentials.commands.weather.RainCommand
import com.mairwunnx.projectessentials.commands.weather.StormCommand
import com.mairwunnx.projectessentials.commands.weather.SunCommand
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.cooldowns.CooldownBase
import com.mairwunnx.projectessentials.cooldowns.processCooldownOfCommand
import com.mairwunnx.projectessentials.extensions.commandName
import com.mairwunnx.projectessentials.extensions.fullName
import com.mairwunnx.projectessentials.extensions.player
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND
import com.mairwunnx.projectessentials.helpers.validateForgeVersion
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mairwunnx.projectessentials.storage.UserData
import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.entity.player.PlayerEvent.*
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

const val MOD_ID = "project_essentials"
const val MOD_NAME = "Project Essentials"
const val MOD_VERSION = "1.14.4-0.1.1.0"
const val MOD_DESCRIPTION = "minecraft command mod - adds commands for use in-game"
const val MOD_MAINTAINER = "MairwunNx (Pavel Erokhin)"
const val MOD_TARGET_FORGE = "28.0.X"
const val MOD_TARGET_FORGE_REGEX = "^28\\.0\\..\\d{1,}|28\\.0\\.[\\d]\$"
const val MOD_TARGET_MC = "1.14.4"
const val MOD_SOURCES_LINK = "https://github.com/MairwunNx/ProjectEssentials/"
const val MOD_TELEGRAM_LINK = "https://t.me/minecraftforge"

@UnstableDefault
@Mod(MOD_ID)
class ProjectEssentials {
    private val logger = LogManager.getLogger()

    init {
        logBaseInfo()
        validateForgeVersion()
        logger.debug("Register event bus for $MOD_NAME mod ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading $MOD_NAME modification settings ...")
        ModConfiguration.loadConfig()
        StorageBase.loadUserData()
    }

    private fun logBaseInfo() {
        logger.info("$MOD_NAME starting initializing ...")
        logger.info("    - Mod Id: $MOD_ID")
        logger.info("    - Version: $MOD_VERSION")
        logger.info("    - Maintainer: $MOD_MAINTAINER")
        logger.info("    - Target Forge version: $MOD_TARGET_FORGE")
        logger.info("    - Target Minecraft version: $MOD_TARGET_MC")
        logger.info("    - Source code: $MOD_SOURCES_LINK")
        logger.info("    - Telegram chat: $MOD_TELEGRAM_LINK")
    }

    @SubscribeEvent
    fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME starting mod loading ...")
        registerCommands(it.server.commandManager.dispatcher)
    }

    private fun registerCommands(
        cmdDispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("Command registering is starting ...")
        HealCommand.register(cmdDispatcher)
        FeedCommand.register(cmdDispatcher)
        TopCommand.register(cmdDispatcher)
        AirCommand.register(cmdDispatcher)
        FlyCommand.register(cmdDispatcher)
        GodCommand.register(cmdDispatcher)
        ListCommand.register(cmdDispatcher)
        BreakCommand.register(cmdDispatcher)
        GetPosCommand.register(cmdDispatcher)
        MoreCommand.register(cmdDispatcher)
        DayCommand.register(cmdDispatcher)
        NightCommand.register(cmdDispatcher)
        MidnightCommand.register(cmdDispatcher)
        NoonCommand.register(cmdDispatcher)
        SunsetCommand.register(cmdDispatcher)
        SunriseCommand.register(cmdDispatcher)
        TimeCommand.register(cmdDispatcher)
        SuicideCommand.register(cmdDispatcher)
        RainCommand.register(cmdDispatcher)
        StormCommand.register(cmdDispatcher)
        SunCommand.register(cmdDispatcher)
        RepairCommand.register(cmdDispatcher)
        EssentialsCommand.register(cmdDispatcher)
    }

    @UnstableDefault
    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down $MOD_NAME mod ...")
        logger.info("    - Saving modification configuration ...")
        ModConfiguration.saveConfig()
        logger.info("    - Saving modification user data ...")
        StorageBase.saveUserData()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (it.parseResults.context.source.entity is ServerPlayerEntity) {
            val commandName = it.commandName
            val commandSender = it.player
            val commandSenderNickName = commandSender.name.string
            val cooldownsConfig = ModConfiguration.getCooldownsConfig()

            if (isBlockedCommand(it)) {
                logger.warn(
                    DISABLED_COMMAND
                        .replace("%0", commandSenderNickName)
                        .replace("%1", commandName)
                )
                sendMsg(commandSender.commandSource, "common.command.blocked")
                it.isCanceled = true
                return
            }

            try {
                if (
                    !cooldownsConfig.ignoredPlayers.contains(commandSenderNickName) &&
                    !commandSender.hasPermissionLevel(cooldownsConfig.bypassPermissionLevel)
                ) {
                    it.isCanceled = processCooldownOfCommand(
                        commandName, commandSenderNickName, it
                    )
                    return
                }
            } catch (_: KotlinNullPointerException) {
                CooldownBase.addCooldown(commandSenderNickName, commandName)
            }
        }
    }

    private fun isBlockedCommand(event: CommandEvent): Boolean {
        val commandName = event.commandName
        val commandConfig = ModConfiguration.getCommandsConfig()
        return when {
            commandConfig.disabledCommands.contains(commandName) -> true
            else -> CommandAliases.searchForAliases(commandName)
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerLoggedInEvent) {
        processAbilities(event.player)
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerLoggedOutEvent) = savePlayerData(event.player)

    @SubscribeEvent
    fun onPlayerChangedDim(event: PlayerChangedDimensionEvent) {
        processAbilities(event.player)
    }

    private fun processAbilities(player: PlayerEntity) {
        val config = ModConfiguration.getCommandsConfig().commands
        val playerCommandSource = player.commandSource
        val serverPlayerEntity = player.commandSource.asPlayer()
        if (config.fly.autoFlyEnabled) {
            if (serverPlayerEntity.hasPermissionLevel(config.fly.permissionLevel)) {
                if (FlyCommand.setFly(serverPlayerEntity, true)) {
                    sendMsg(playerCommandSource, "fly.auto.success")
                }
            }
        }
        if (config.god.autoGodModeEnabled) {
            if (serverPlayerEntity.hasPermissionLevel(config.god.permissionLevel)) {
                if (GodCommand.setGod(serverPlayerEntity, true)) {
                    sendMsg(playerCommandSource, "god.auto.success")
                }
            }
        }
    }

    private fun savePlayerData(player: PlayerEntity) {
        val uuid = player.gameProfile.id
        val uuidString = uuid.toString()
        StorageBase.setData(
            uuidString, UserData(
                player.world.fullName(),
                "${player.posX.toInt()}, ${player.posY.toInt()}, ${player.posZ.toInt()}",
                getFlyEnabledWorlds(player, StorageBase.getData(uuidString).flyEnabledInWorlds),
                getGodEnabledWorlds(player, StorageBase.getData(uuidString).godEnabledWorlds)
            )
        )
    }

    private fun getFlyEnabledWorlds(
        player: PlayerEntity,
        flyAbleWorlds: List<String>
    ): List<String> {
        if (getFly(player)) {
            if (!flyAbleWorlds.contains(player.world.worldInfo.worldName)) {
                val list = flyAbleWorlds.toMutableList()
                list.add(player.world.worldInfo.worldName)
                return list
            }
        } else {
            val list = flyAbleWorlds.toMutableList()
            list.remove(player.world.worldInfo.worldName)
            return list
        }
        return flyAbleWorlds
    }

    private fun getGodEnabledWorlds(
        player: PlayerEntity,
        godAbleWorlds: List<String>
    ): List<String> {
        if (getGod(player)) {
            if (!godAbleWorlds.contains(player.world.worldInfo.worldName)) {
                val list = godAbleWorlds.toMutableList()
                list.add(player.world.worldInfo.worldName)
                return list
            }
        } else {
            val list = godAbleWorlds.toMutableList()
            list.remove(player.world.worldInfo.worldName)
            return list
        }
        return godAbleWorlds
    }

    private fun getFly(player: PlayerEntity?): Boolean {
        if (player == null) return false
        return if (player.onGround) {
            player.abilities.allowFlying
        } else {
            player.abilities.isFlying || player.abilities.allowFlying
        }
    }

    private fun getGod(player: PlayerEntity?): Boolean {
        if (player == null) return false
        return player.abilities.disableDamage
    }
}
