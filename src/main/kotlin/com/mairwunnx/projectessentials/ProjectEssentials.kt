@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.CommandAliases
import com.mairwunnx.projectessentials.commands.CommandsBase
import com.mairwunnx.projectessentials.commands.FlyCommand
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.cooldowns.CooldownBase
import com.mairwunnx.projectessentials.cooldowns.processCooldownOfCommand
import com.mairwunnx.projectessentials.extensions.commandName
import com.mairwunnx.projectessentials.extensions.player
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val MOD_ID = "project_essentials"
const val MOD_NAME = "Project Essentials"
const val MOD_VERSION = "1.14.4-0.0.5.0"
const val MOD_DESCRIPTION = "minecraft command mod - adds commands for use in-game"
const val MOD_MAINTAINER = "MairwunNx (Pavel Erokhin)"
const val MOD_TARGET_FORGE = "28.0.X"
const val MOD_TARGET_MC = "1.14.4"
const val MOD_SOURCES_LINK = "https://github.com/MairwunNx/ProjectEssentials/"
const val MOD_TELEGRAM_LINK = "https://t.me/minecraftforge"

lateinit var commandsBase: CommandsBase
lateinit var commandDispatcher: CommandDispatcher<CommandSource>

@Mod(MOD_ID)
class ProjectEssentials {
    private val logger: Logger = LogManager.getLogger()

    init {
        logger.info("$MOD_NAME $MOD_VERSION starting initializing ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading ProjectEssentials mod settings ...")
        ModConfiguration.loadConfig()
    }

    @SubscribeEvent
    fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME $MOD_VERSION starting mod loading ...")
        commandsBase = CommandsBase()
        commandDispatcher = it.server.commandManager.dispatcher
        commandsBase.registerAll(it.server.commandManager.dispatcher)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down Project Essentials mod ...")
        logger.info("    - Saving configuration ...")
        ModConfiguration.saveConfig()
        logger.info("Done, thanks for using")
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
                it.isCanceled = true
            }

            try {
                if (
                    !cooldownsConfig.ignoredPlayers.contains(commandSenderNickName) &&
                    !commandSender.hasPermissionLevel(cooldownsConfig.bypassPermissionLevel)
                ) {
                    it.isCanceled = processCooldownOfCommand(
                        commandName, commandSenderNickName, it
                    )
                }
            } catch (_: KotlinNullPointerException) {
                CooldownBase.addCooldown(
                    commandSenderNickName,
                    commandName
                )
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
        val config = ModConfiguration.getCommandsConfig().commands
        if (config.fly.autoFlyEnabled) {
            if (config.fly.autoFly.contains(event.player.name.string)) {
                if (event.player.commandSource.asPlayer().hasPermissionLevel(
                        config.fly.permissionLevel
                    )
                ) {
                    if (FlyCommand.setFly(event.player.commandSource.asPlayer(), true)) {
                        sendMsg(event.player.commandSource, "fly.auto.success")
                    }
                }
            }
        }
    }
}
