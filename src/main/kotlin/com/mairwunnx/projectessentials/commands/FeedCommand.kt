package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.extensions.isNeedFood
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class FeedCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val FEED_COMMAND: String = "feed"
        private const val FEED_ARG_NAME_COMMAND: String = "player"
        private val FeedCommandAliases: Array<String> = arrayOf(
            FEED_COMMAND, "eat", "eeat", "efeed"
        )

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$FEED_COMMAND\" command ...")

            FeedCommandAliases.forEach { command ->
                dispatcher.register(
                    LiteralArgumentBuilder.literal<CommandSource>(command)
                        .then(
                            RequiredArgumentBuilder.argument<CommandSource, String>(
                                FEED_ARG_NAME_COMMAND,
                                StringArgumentType.string()
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
            val sender: String = c.source.asPlayer().name.string

            if (!c.source.asPlayer().hasPermissionLevel(2)) {
                logger.info("Player ($sender) failed to executing \"/$FEED_COMMAND\" command")

                if (hasTarget) {
                    val player: String = getString(c, FEED_ARG_NAME_COMMAND)
                    c.source.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.error", player
                        ),
                        true
                    )
                } else {
                    c.source.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.self.error"
                        ),
                        true
                    )
                }

                return
            }

            logger.info("Executed command \"/$FEED_COMMAND\" from $sender")
            if (hasTarget) {
                val player: String = getString(c, FEED_ARG_NAME_COMMAND)
                c.source.world.players.forEach { targetAsPlayer ->
                    if (targetAsPlayer.name.string != player ||
                        targetAsPlayer.hasDisconnected()
                    ) {
                        c.source.sendFeedback(
                            TranslationTextComponent(
                                "projectessentials.common.player.notonline", player
                            ),
                            true
                        )
                        return
                    }
                    if (!targetAsPlayer.foodStats.isNeedFood()) {
                        c.source.sendFeedback(
                            TranslationTextComponent(
                                "projectessentials.feed.player.maxfeed", player
                            ),
                            true
                        )
                        return
                    }
                    logger.info(
                        "Player ($player) food level/saturation changed from ${targetAsPlayer.foodStats.foodLevel}/${targetAsPlayer.foodStats.saturationLevel} to 20/5.0 by $sender"
                    )
                    targetAsPlayer.foodStats.foodLevel = 20
                    targetAsPlayer.foodStats.setFoodSaturationLevel(5.0f)
                    c.source.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.success", player
                        ),
                        true
                    )
                    targetAsPlayer.commandSource.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.recipient.success", sender
                        ),
                        true
                    )
                }
            } else {
                if (!c.source.asPlayer().foodStats.isNeedFood()) {
                    c.source.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.self.maxfeed"
                        ),
                        true
                    )
                    return
                }
                logger.info(
                    "Player ($sender) food level/saturation changed from ${c.source.asPlayer().foodStats.foodLevel}/${c.source.asPlayer().foodStats.saturationLevel} to 20/5.0"
                )
                c.source.asPlayer().foodStats.foodLevel = 20
                c.source.asPlayer().foodStats.setFoodSaturationLevel(5.0f)
                c.source.sendFeedback(
                    TranslationTextComponent(
                        "projectessentials.feed.self.success"
                    ),
                    true
                )
            }
        }
    }
}
