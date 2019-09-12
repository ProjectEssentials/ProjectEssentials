package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.commandDispatcher
import com.mairwunnx.projectessentials.commandsBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.enums.EssentialsCommandArgs
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class EssentialsCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val ESSENTIALS_COMMAND: String = "essentials"
        private val essentialsCommandAliases: MutableList<String> = mutableListOf(
            ESSENTIALS_COMMAND, "ess", "eessentials", "eess"
        )

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$ESSENTIALS_COMMAND\" command ...")
            logger.info("Processing commands aliases for \"/$ESSENTIALS_COMMAND\" command ...")

            CommandAliases.aliases[ESSENTIALS_COMMAND] = essentialsCommandAliases
            essentialsCommandAliases.forEach { command ->
                dispatcher.register(
                    LiteralArgumentBuilder.literal<CommandSource>(command)
                        .then(
                            RequiredArgumentBuilder.argument<CommandSource, String>(
                                "argument", string()
                            ).executes {
                                execute(it)
                                return@executes 1
                            }
                        )
                        .executes {
                            execute(it)
                            return@executes 1
                        }
                )
            }
        }

        private fun execute(
            c: CommandContext<CommandSource>,
            arg: EssentialsCommandArgs = EssentialsCommandArgs.RELOAD
        ) {
            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!c.isPlayerSender() ||
                c.source.asPlayer().hasPermissionLevel(4)
            ) {
                if (arg == EssentialsCommandArgs.RELOAD) {
                    reload(commandSender)
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "project_essentials.common.reload.success"
                        ), true
                    )
                }
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", ESSENTIALS_COMMAND)
                )
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "project_essentials.common.reload.error"
                    ), true
                )
            }
        }

        private fun reload(commandSender: CommandSource) {
            logger.info("Starting reloading configuration ...")
            ModConfiguration.loadConfig()
            logger.info("Starting reloading commands ...")
            commandsBase.registerAll(commandDispatcher)
            logger.info("Configuration successfully reloaded")
        }
    }
}
