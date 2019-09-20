package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.*
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

@UnstableDefault
object EssentialsCommand {
    private val logger = LogManager.getLogger()
    private const val ESSENTIALS_COMMAND = "essentials"
    private const val ESSENTIALS_COMMAND_VERSION = "version"
    private const val ESSENTIALS_COMMAND_SAVE = "save"
    private const val ESSENTIALS_COMMAND_RELOAD = "reload"

    private fun buildEssentialsCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal(ESSENTIALS_COMMAND).executes {
            executeVersionCommand(it)
        }
    }

    private fun buildEssentialsReloadCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal(ESSENTIALS_COMMAND_RELOAD).executes {
            executeReloadCommand(it)
        }
    }

    private fun executeReloadCommand(it: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = it.source
        val commandSenderNickName = if (it.isPlayerSender()) {
            it.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender || it.source.asPlayer().hasPermissionLevel(
                ModConfiguration.getCommandsConfig().essentialsCommands.reloadPermissionLevel
            )
        ) {
            ModConfiguration.loadConfig()
            GodCommand.reload()
            if (isServerSender) {
                logger.info("Successfully reloaded Project Essentials configuration")
            } else {
                sendMsg(commandSender, "common.reload.success")
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_RELOAD")
            )
            sendMsg(commandSender, "common.reload.error")
            return 0
        }
    }

    @UnstableDefault
    private fun buildEssentialsSaveCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal(ESSENTIALS_COMMAND_SAVE).executes {
            executeSaveCommand(it)
        }
    }

    @UnstableDefault
    private fun executeSaveCommand(it: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = it.source
        val commandSenderNickName = if (it.isPlayerSender()) {
            it.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender || it.source.asPlayer().hasPermissionLevel(
                ModConfiguration.getCommandsConfig().essentialsCommands.savePermissionLevel
            )
        ) {
            ModConfiguration.saveConfig()
            StorageBase.saveUserData()
            if (isServerSender) {
                logger.info("Successfully saved Project Essentials configuration")
            } else {
                sendMsg(commandSender, "common.save.success")
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_SAVE")
            )
            sendMsg(commandSender, "common.save.error")
            return 0
        }
    }

    private fun buildEssentialsVersionCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal(ESSENTIALS_COMMAND_VERSION).executes {
            executeVersionCommand(it)
        }
    }

    private fun executeVersionCommand(it: CommandContext<CommandSource>): Int {
        val commandsConfig = ModConfiguration.getCommandsConfig()
        var isServerSender = false
        val commandSender = it.source
        val commandSenderNickName = if (it.isPlayerSender()) {
            it.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (isServerSender || it.source.asPlayer().hasPermissionLevel(
                commandsConfig.essentialsCommands.versionPermissionLevel
            )
        ) {
            if (isServerSender) {
                logger.info("        $MOD_NAME")
                logger.info("Version: $MOD_VERSION")
                logger.info("Maintainer: $MOD_MAINTAINER")
                logger.info("Target Forge version: $MOD_TARGET_FORGE")
                logger.info("Target Minecraft version: $MOD_TARGET_MC")
                logger.info("Source code: $MOD_SOURCES_LINK")
                logger.info("Telegram chat: $MOD_TELEGRAM_LINK")
            } else {
                sendMsg(
                    commandSender,
                    "common.version.success",
                    MOD_NAME,
                    MOD_VERSION,
                    MOD_MAINTAINER,
                    MOD_TARGET_FORGE,
                    MOD_TARGET_MC,
                    MOD_SOURCES_LINK,
                    MOD_TELEGRAM_LINK
                )
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "$ESSENTIALS_COMMAND $ESSENTIALS_COMMAND_VERSION")
            )
            sendMsg(commandSender, "common.version.error")
            return 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("    - register \"/$ESSENTIALS_COMMAND\" command ...")

        dispatcher.register(
            buildEssentialsCommand() as LiteralArgumentBuilder<CommandSource>?
        )
        dispatcher.register(
            buildEssentialsCommand()?.then(
                buildEssentialsReloadCommand()
            ) as LiteralArgumentBuilder<CommandSource>?
        )
        dispatcher.register(
            buildEssentialsCommand()?.then(
                buildEssentialsSaveCommand()
            ) as LiteralArgumentBuilder<CommandSource>?
        )
        dispatcher.register(
            buildEssentialsCommand()?.then(
                buildEssentialsVersionCommand()
            ) as LiteralArgumentBuilder<CommandSource>?
        )
    }
}
