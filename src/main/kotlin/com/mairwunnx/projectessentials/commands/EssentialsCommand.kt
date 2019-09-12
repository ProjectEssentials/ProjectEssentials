package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.MOD_MAINTAINER
import com.mairwunnx.projectessentials.MOD_NAME
import com.mairwunnx.projectessentials.MOD_SOURCES_LINK
import com.mairwunnx.projectessentials.MOD_TARGET_FORGE
import com.mairwunnx.projectessentials.MOD_TARGET_MC
import com.mairwunnx.projectessentials.MOD_TELEGRAM_LINK
import com.mairwunnx.projectessentials.MOD_VERSION
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
        return 1
    }

    private fun buildEssentialsSaveCommand(): ArgumentBuilder<CommandSource, *>? {
        return Commands.literal("save").executes {
            executeSaveCommand(it)
        }
    }

    private fun executeSaveCommand(it: CommandContext<CommandSource>): Int {
        return 1
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
                logger.info(MOD_NAME)
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

//    private fun reload(commandSender: CommandSource) {
//        logger.info("Starting reloading configuration ...")
//        ModConfiguration.loadConfig()
//        logger.info("Starting reloading commands ...")
//        commandsBase.registerAll(commandDispatcher)
//        logger.info("Configuration successfully reloaded")
//    }
}
