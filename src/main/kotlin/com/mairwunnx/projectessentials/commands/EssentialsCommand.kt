package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.*
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object EssentialsCommand {
    private val logger: Logger = LogManager.getLogger()
    private const val ESSENTIALS_COMMAND: String = "essentials"
    private val essentialsCommandAliases: MutableList<String> = mutableListOf(
        ESSENTIALS_COMMAND, "ess", "eessentials", "eess"
    )

    private fun buildEssentialsCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal("essentials").executes {
            executeVersionCommand(it)
        }
    }

    private fun buildEssentialsReloadCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal("reload").executes {
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

        if (it.source.asPlayer().hasPermissionLevel(
                ModConfiguration.getCommandsConfig().essentialsCommands.reloadPermissionLevel
            ) || isServerSender
        ) {
            ModConfiguration.loadConfig()
            if (isServerSender) {
                logger.info("Successfully reloaded Project Essentials configuration")
            } else {
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "project_essentials.common.reload.success"
                    ), false
                )
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "essentials reload")
            )
            commandSender.sendFeedback(
                TranslationTextComponent(
                    "project_essentials.common.reload.error"
                ), false
            )
            return 0
        }
    }

    private fun buildEssentialsSaveCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal("save").executes {
            executeSaveCommand(it)
        }
    }

    private fun executeSaveCommand(it: CommandContext<CommandSource>): Int {
        var isServerSender = false
        val commandSender = it.source
        val commandSenderNickName = if (it.isPlayerSender()) {
            it.source.asPlayer().name.string
        } else {
            isServerSender = true
            "server"
        }

        if (it.source.asPlayer().hasPermissionLevel(
                ModConfiguration.getCommandsConfig().essentialsCommands.savePermissionLevel
            ) || isServerSender
        ) {
            ModConfiguration.saveConfig()
            if (isServerSender) {
                logger.info("Successfully saved Project Essentials configuration")
            } else {
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "project_essentials.common.save.success"
                    ), false
                )
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "essentials save")
            )
            commandSender.sendFeedback(
                TranslationTextComponent(
                    "project_essentials.common.save.error"
                ), false
            )
            return 0
        }
    }

    private fun buildEssentialsVersionCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal("version").executes {
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

        if (it.source.asPlayer().hasPermissionLevel(
                commandsConfig.essentialsCommands.versionPermissionLevel
            ) || isServerSender
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
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "project_essentials.common.version.success",
                        MOD_NAME,
                        MOD_VERSION,
                        MOD_MAINTAINER,
                        MOD_TARGET_FORGE,
                        MOD_TARGET_MC,
                        MOD_SOURCES_LINK,
                        MOD_TELEGRAM_LINK
                    ), true
                )
            }
            return 1
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", "essentials version")
            )
            commandSender.sendFeedback(
                TranslationTextComponent(
                    "project_essentials.common.version.error"
                ),
                true
            )
            return 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("Starting register \"/$ESSENTIALS_COMMAND\" command ...")
        logger.info("Processing commands aliases for \"/$ESSENTIALS_COMMAND\" command ...")

        CommandAliases.aliases[ESSENTIALS_COMMAND] = essentialsCommandAliases

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
