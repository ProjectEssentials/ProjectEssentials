package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.isPlayerSender
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

/**
 * **Description:** Heals you or the given player.
 *
 * **Usage example:** `/heal` and `/eheal`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HealCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val HEAL_COMMAND: String = "heal"
        private const val HEAL_ARG_NAME_COMMAND: String = "player"
        private val healCommandAliases: MutableList<String> = mutableListOf(HEAL_COMMAND)

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            val modConfig = ModConfiguration.getCommandsConfig()
            logger.info("Starting register \"/$HEAL_COMMAND\" command ...")
            logger.info("Processing commands aliases for \"/$HEAL_COMMAND\" command ...")

            healCommandAliases.addAll(
                modConfig.commands.heal.aliases
            )

            registerAliases()

            healCommandAliases.forEach { command ->
                dispatcher.register(
                    literal<CommandSource>(command)
                        .then(
                            RequiredArgumentBuilder.argument<CommandSource, String>(
                                HEAL_ARG_NAME_COMMAND, string()
                            ).executes {
                                if (modConfig.commands.heal.enableArgs) {
                                    execute(it, true)
                                } else {
                                    execute(it)
                                }
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
            val modConfig = ModConfiguration.getCommandsConfig()

            if (!c.isPlayerSender()) {
                logger.warn(
                    "\"/${HEAL_COMMAND}\" command should only be used by the player!"
                )
                return
            }

            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!commandSender.asPlayer().hasPermissionLevel(
                    modConfig.commands.heal.permissionLevel
                )
            ) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", HEAL_COMMAND)
                )

                if (hasTarget) {
                    val playerNickNameAsTarget: String = getString(c, HEAL_ARG_NAME_COMMAND)
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "project_essentials.heal.player.error",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                } else {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "project_essentials.heal.self.error"
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
                                "project_essentials.common.player.notonline",
                                playerNickNameAsTarget
                            ),
                            true
                        )
                        return
                    }
                    if (targetAsPlayer.health == targetAsPlayer.maxHealth) {
                        commandSender.sendFeedback(
                            TranslationTextComponent(
                                "project_essentials.heal.player.maxhealth",
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
                            "project_essentials.heal.player.success",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                    targetAsPlayer.commandSource.sendFeedback(
                        TranslationTextComponent(
                            "project_essentials.heal.player.recipient.success",
                            commandSenderNickName
                        ),
                        true
                    )
                }
            } else {
                if (commandSender.asPlayer().health == commandSender.asPlayer().maxHealth) {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "project_essentials.heal.self.maxhealth"
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
                        "project_essentials.heal.self.success"
                    ),
                    true
                )
            }
        }

        private fun registerAliases() {
            CommandAliases.aliases[HEAL_COMMAND] = healCommandAliases
        }
    }
}
