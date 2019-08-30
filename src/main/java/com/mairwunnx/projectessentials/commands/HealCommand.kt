package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class HealCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val HEAL_COMMAND: String = "heal"
        private const val HEAL_ARG_NAME_COMMAND: String = "player"

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$HEAL_COMMAND\" command ...")

            dispatcher.register(
                literal<CommandSource>(HEAL_COMMAND)
                    .then(
                        argument<CommandSource, String>(HEAL_ARG_NAME_COMMAND, string())
                            .executes {
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

            if (!c.source.asPlayer().hasPermissionLevel(2)) return
            logger.info("Executed command $HEAL_COMMAND from $sender")

            if (hasTarget) {
                val player: String = getString(c, HEAL_ARG_NAME_COMMAND)
                c.source.world.players.forEach {
                    if (it.name.string != player || it.hasDisconnected()) return
                    logger.info(
                        "Player ($player) Health changed from ${c.source.asPlayer().health} to ${c.source.asPlayer().maxHealth} by $sender"
                    )
                    it.health = it.maxHealth
                }
            } else {
                if (c.source.asPlayer().health != c.source.asPlayer().maxHealth) {
                    logger.info(
                        "Player ($sender) Health changed from ${c.source.asPlayer().health} to ${c.source.asPlayer().maxHealth}"
                    )
                    c.source.asPlayer().health = c.source.asPlayer().maxHealth
                }
            }
        }
    }
}
