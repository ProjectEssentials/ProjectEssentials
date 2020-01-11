package com.mairwunnx.projectessentials.commands.staff

import com.mairwunnx.projectessentials.ProjectEssentials
import com.mairwunnx.projectessentials.commands.abilities.FlyCommand
import com.mairwunnx.projectessentials.commands.abilities.GodCommand
import com.mairwunnx.projectessentials.commands.general.*
import com.mairwunnx.projectessentials.commands.health.AirCommand
import com.mairwunnx.projectessentials.commands.health.FeedCommand
import com.mairwunnx.projectessentials.commands.health.HealCommand
import com.mairwunnx.projectessentials.commands.moderator.GetPosCommand
import com.mairwunnx.projectessentials.commands.teleport.*
import com.mairwunnx.projectessentials.commands.time.*
import com.mairwunnx.projectessentials.commands.weather.RainCommand
import com.mairwunnx.projectessentials.commands.weather.StormCommand
import com.mairwunnx.projectessentials.commands.weather.SunCommand
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mairwunnx.projectessentialscore.extensions.isPlayerSender
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

object EssentialsCommand {
    private val logger = LogManager.getLogger()
    private const val ESSENTIALS_COMMAND = "essentials"
    private const val ESSENTIALS_COMMAND_VERSION = "version"
    private const val ESSENTIALS_COMMAND_SAVE = "save"
    private const val ESSENTIALS_COMMAND_RELOAD = "reload"

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("    - register \"/$ESSENTIALS_COMMAND\" command ...")

        dispatcher.register(
            literal<CommandSource>("essentials").executes {
                return@executes versionExecute(it)
            }.then(Commands.literal("reload").executes {
                return@executes reloadExecute(it)
            }).then(Commands.literal("save").executes {
                return@executes saveExecute(it)
            }).then(Commands.literal("version").executes {
                return@executes versionExecute(it)
            })
        )
    }

    private fun versionExecute(c: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = c.source
        val commandSenderNickName = if (c.isPlayerSender()) {
            c.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.version") ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.stuff")
        ) {
            if (isServerSender) {
                logger.info("        ${ProjectEssentials.modInstance.modName}")
                logger.info("Version: ${ProjectEssentials.modInstance.modVersion}")
                logger.info("Maintainer: ${ProjectEssentials.modInstance.modMaintainer}")
                logger.info("Target Forge version: ${ProjectEssentials.modInstance.modTargetForge}")
                logger.info("Target Minecraft version: ${ProjectEssentials.modInstance.modTargetMC}")
                logger.info("Source code: ${ProjectEssentials.modInstance.modSources}")
                logger.info("Telegram chat: ${ProjectEssentials.modInstance.modTelegram}")
            } else {
                sendMsg(
                    commandSender,
                    "common.version.success",
                    ProjectEssentials.modInstance.modName,
                    ProjectEssentials.modInstance.modVersion,
                    ProjectEssentials.modInstance.modMaintainer,
                    ProjectEssentials.modInstance.modTargetForge,
                    ProjectEssentials.modInstance.modTargetMC,
                    ProjectEssentials.modInstance.modSources,
                    ProjectEssentials.modInstance.modTelegram
                )
            }
            return 0
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_VERSION")
            )
            sendMsg(commandSender, "common.version.restricted")
            return 0
        }
    }

    private fun reloadExecute(c: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = c.source
        val commandSenderNickName = if (c.isPlayerSender()) {
            c.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.reload") ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.stuff")
        ) {
            ModConfiguration.loadConfig()
            reloadCommandsConfigs()
            if (isServerSender) {
                logger.info("Successfully reloaded Project Essentials configuration")
            } else {
                sendMsg(commandSender, "common.reload.success")
            }
            return 0
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_RELOAD")
            )
            sendMsg(commandSender, "common.reload.restricted")
            return 0
        }
    }

    private fun reloadCommandsConfigs() {
        AirCommand.reload()
        HealCommand.reload()
        TopCommand.reload()
        FeedCommand.reload()
        FlyCommand.reload()
        GodCommand.reload()
        ListCommand.reload()
        BreakCommand.reload()
        GetPosCommand.reload()
        SendPosCommand.reload()
        MoreCommand.reload()
        DayCommand.reload()
        NightCommand.reload()
        MidnightCommand.reload()
        NoonCommand.reload()
        SunsetCommand.reload()
        SunriseCommand.reload()
        TimeCommand.reload()
        SuicideCommand.reload()
        RainCommand.reload()
        StormCommand.reload()
        SunCommand.reload()
        RepairCommand.reload()
        PingCommand.reload()
        AfkCommand.reload()
        BurnCommand.reload()
        LightningCommand.reload()
        TpPosCommand.reload()
        TpAllCommand.reload()
        TpHereCommand.reload()
        TpaCommand.reload()
        TpAcceptCommand.reload()
        TpaHereCommand.reload()
        TpDenyCommand.reload()
        TpToggleCommand.reload()
        TpaCancelCommand.reload()
        ProjectEssentials.teleportPresenter.configureTimeOut()
    }

    private fun saveExecute(c: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = c.source
        val commandSenderNickName = if (c.isPlayerSender()) {
            c.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.save") ||
            PermissionsAPI.hasPermission(commandSenderNickName, "ess.stuff")
        ) {
            ModConfiguration.saveConfig()
            StorageBase.saveUserData()
            if (isServerSender) {
                logger.info("Successfully saved Project Essentials configuration")
            } else {
                sendMsg(commandSender, "common.save.success")
            }
            return 0
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_SAVE")
            )
            sendMsg(commandSender, "common.save.restricted")
            return 0
        }
    }
}
