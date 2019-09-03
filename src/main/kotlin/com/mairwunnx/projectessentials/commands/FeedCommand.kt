package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class FeedCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val FEED_COMMAND: String = "feed"
        private const val FEED_ARG_NAME_COMMAND: String = "player"

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$FEED_COMMAND\" command ...")

            dispatcher.register(
                LiteralArgumentBuilder.literal<CommandSource>(FEED_COMMAND)
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
                        execute(it, false)
                        return@executes 1
                    }
            )
        }

        private fun execute(c: CommandContext<CommandSource>, hasTarget: Boolean) {
            val sender: String = c.source.asPlayer().name.string

            if (!c.source.asPlayer().hasPermissionLevel(2)) {
                logger.info("Player ($sender) failed to executing \"/$FEED_COMMAND\" command")
                return
            }

            logger.info("Executed command \"/$FEED_COMMAND\" from $sender")
            if (hasTarget) {
                val player: String =
                    StringArgumentType.getString(c, FEED_ARG_NAME_COMMAND)
                c.source.world.players.forEach {
                    if (it.name.string != player || it.hasDisconnected()) return
                    if (!it.foodStats.needFood()) return
                    it.foodStats.foodLevel = 20
                    it.foodStats.setFoodSaturationLevel(5.0f)
                }
            } else {
                if (!c.source.asPlayer().foodStats.needFood()) return
                c.source.asPlayer().foodStats.foodLevel = 20
                c.source.asPlayer().foodStats.setFoodSaturationLevel(5.0f)
            }
        }
    }
}
