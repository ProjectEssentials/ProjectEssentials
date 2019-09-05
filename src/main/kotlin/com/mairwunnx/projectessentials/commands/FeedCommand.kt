package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.extensions.isNeedFood
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.lang.reflect.Field
import net.minecraft.command.CommandSource
import net.minecraft.util.FoodStats
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * **Description:** Satisfy the hunger of you or the given player.
 *
 * **Usage example:** `/feed`, `/eat`, `/eeat` and `/efeed`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 */
class FeedCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val FEED_COMMAND: String = "feed"
        private const val FEED_ARG_NAME_COMMAND: String = "player"
        private val feedCommandAliases: Array<String> = arrayOf(
            FEED_COMMAND, "eat", "eeat", "efeed"
        )
        private val saturationLevel: Field by lazy {
            return@lazy ObfuscationReflectionHelper.findField(
                FoodStats::class.java,
                "field_75125_b"
            )
        }

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$FEED_COMMAND\" command ...")

            feedCommandAliases.forEach { command ->
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
            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!commandSender.asPlayer().hasPermissionLevel(2)) {
                logger.info(
                    "Player ($commandSenderNickName) failed to executing \"/$FEED_COMMAND\" command"
                )

                if (hasTarget) {
                    val playerNickNameAsTarget: String = getString(c, FEED_ARG_NAME_COMMAND)
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.error",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                } else {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.self.error"
                        ),
                        true
                    )
                }

                return
            }

            logger.info("Executed command \"/$FEED_COMMAND\" from $commandSenderNickName")
            if (hasTarget) {
                val playerNickNameAsTarget: String = getString(c, FEED_ARG_NAME_COMMAND)
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
                    if (!targetAsPlayer.foodStats.isNeedFood()) {
                        commandSender.sendFeedback(
                            TranslationTextComponent(
                                "projectessentials.feed.player.maxfeed",
                                playerNickNameAsTarget
                            ),
                            true
                        )
                        return
                    }
                    logger.info(
                        "Player ($playerNickNameAsTarget) food level/saturation changed from ${targetAsPlayer.foodStats.foodLevel}/${targetAsPlayer.foodStats.saturationLevel} to 20/5.0 by $commandSenderNickName"
                    )
                    targetAsPlayer.foodStats.foodLevel = 20

                    val clientSideSaturationLevel = Runnable {
                        targetAsPlayer.foodStats.setFoodSaturationLevel(5.0f)
                    }

                    val serverSideSaturationLevel = Runnable {
                        saturationLevel.setFloat(targetAsPlayer.foodStats, 5.0f)
                    }

                    DistExecutor.runWhenOn(Dist.CLIENT) { clientSideSaturationLevel }
                    DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) { serverSideSaturationLevel }

                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.success",
                            playerNickNameAsTarget
                        ),
                        true
                    )
                    targetAsPlayer.commandSource.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.player.recipient.success",
                            commandSenderNickName
                        ),
                        true
                    )
                }
            } else {
                if (!commandSender.asPlayer().foodStats.isNeedFood()) {
                    commandSender.sendFeedback(
                        TranslationTextComponent(
                            "projectessentials.feed.self.maxfeed"
                        ),
                        true
                    )
                    return
                }
                logger.info(
                    "Player ($commandSenderNickName) food level/saturation changed from ${commandSender.asPlayer().foodStats.foodLevel}/${commandSender.asPlayer().foodStats.saturationLevel} to 20/5.0"
                )
                commandSender.asPlayer().foodStats.foodLevel = 20

                val clientSideSaturationLevel = Runnable {
                    commandSender.asPlayer().foodStats.setFoodSaturationLevel(5.0f)
                }

                val serverSideSaturationLevel = Runnable {
                    saturationLevel.setFloat(commandSender.asPlayer().foodStats, 5.0f)
                }

                DistExecutor.runWhenOn(Dist.CLIENT) { clientSideSaturationLevel }
                DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) { serverSideSaturationLevel }

                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "projectessentials.feed.self.success"
                    ),
                    true
                )
            }
        }
    }
}
