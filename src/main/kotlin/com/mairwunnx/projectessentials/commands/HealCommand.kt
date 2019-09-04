package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HealCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val HEAL_COMMAND: String = "heal"
        private const val HEAL_ARG_NAME_COMMAND: String = "player"
        private val healCommandAliases: Array<String> = arrayOf(HEAL_COMMAND, "eheal")

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$HEAL_COMMAND\" command ...")

            healCommandAliases.forEach { command ->
                dispatcher.register(
                    literal<CommandSource>(command)
                        .then(
                            RequiredArgumentBuilder.argument<CommandSource, String>(
                                HEAL_ARG_NAME_COMMAND, string()
                            ).executes {
                                execute(it, true)
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

        private fun execute(c: CommandContext<CommandSource>, hasTarget: Boolean = false) {
            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!commandSender.asPlayer().hasPermissionLevel(2)) {
                logger.info(
                    "Player ($commandSenderNickName) failed to executing \"/$HEAL_COMMAND\" command"
                )

                if (hasTarget) {
                    val playerNickNameAsTarget: String = getString(c, HEAL_ARG_NAME_COMMAND)
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.heal.player.error",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                } else {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.heal.self.error"
                        ),
                        true
                    )
                }

                return
            }

            logger.info("Executed command \"/$HEAL_COMMAND\" from $commandSenderNickName")
            if (hasTarget) {
                val playerNickNameAsTarget: String = getString(c, HEAL_ARG_NAME_COMMAND)
                commandSender.world.players.forEach { targetAsPlayer ->
                    if (targetAsPlayer.name.string != playerNickNameAsTarget ||
                        targetAsPlayer.hasDisconnected()
                    ) {
                        commandSender.sendFeedback(
                            TranslationTextComponent(
                                "projectessentials.common.player.notonline",
                                playerNickNameAsTarget
                            ),
                            true
                        )
                        return
                    }
                    if (targetAsPlayer.health == targetAsPlayer.maxHealth) {
                        commandSender.sendFeedback(
                            TranslationTextComponent(
                                "projectessentials.heal.player.maxhealth",
                                playerNickNameAsTarget
                            ),
                            true
                        )
                        return
                    }
                    logger.info(
                        "Player ($playerNickNameAsTarget) Health changed from ${commandSender.asPlayer().health} to ${commandSender.asPlayer().maxHealth} by $commandSenderNickName"
                    )
                    targetAsPlayer.health = targetAsPlayer.maxHealth
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.heal.player.success",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                    targetAsPlayer.commandSource.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.heal.player.recipient.success",
                            commandSenderNickName
                        ),
                        true
                    )
                }
            } else {
                if (commandSender.asPlayer().health == commandSender.asPlayer().maxHealth) {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.heal.self.maxhealth"
                        ),
                        true
                    )
                    return
                }
                logger.info(
                    "Player ($commandSenderNickName) Health changed from ${commandSender.asPlayer().health} to ${commandSender.asPlayer().maxHealth}"
                )
                commandSender.asPlayer().health = commandSender.asPlayer().maxHealth
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "projectessentials.heal.self.success"
                    ),
                    true
                )
            }
        }
    }
}
