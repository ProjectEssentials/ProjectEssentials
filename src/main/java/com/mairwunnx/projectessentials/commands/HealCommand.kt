package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.extensions.sendMessage
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.resources.I18n.format
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HealCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val HEAL_COMMAND: String = "heal"
        private const val HEAL_ARG_NAME_COMMAND: String = "player"
        private val HealCommandAliases: Array<String> = arrayOf(HEAL_COMMAND, "eheal")

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$HEAL_COMMAND\" command ...")

            HealCommandAliases.forEach { command ->
                dispatcher.register(
                    literal<CommandSource>(command)
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
        }

        private fun execute(c: CommandContext<CommandSource>, hasTarget: Boolean) {
            val sender: String = c.source.asPlayer().name.string

            if (!c.source.asPlayer().hasPermissionLevel(2)) {
                logger.info("Player ($sender) failed to executing \"/$HEAL_COMMAND\" command")

                if (hasTarget) {
                    val player: String = getString(c, HEAL_ARG_NAME_COMMAND)
                    c.source.asPlayer().sendMessage(
                        healErrorPlayerString.replace("{0}", player)
                    )
                } else {
                    c.source.asPlayer().sendMessage(healErrorSelfString)
                }

                return
            }

            logger.info("Executed command \"/$HEAL_COMMAND\" from $sender")
            if (hasTarget) {
                val player: String = getString(c, HEAL_ARG_NAME_COMMAND)
                c.source.world.players.forEach {
                    if (it.name.string != player || it.hasDisconnected()) {
                        c.source.asPlayer().sendMessage(
                            errorPlayerNotOnlineString.replace("{0}", player)
                        )
                        return
                    }
                    if (it.health == it.maxHealth) {
                        c.source.asPlayer().sendMessage(
                            healedFullErrorPlayerString.replace("{0}", player)
                        )
                        return
                    }
                    logger.info(
                        "Player ($player) Health changed from ${c.source.asPlayer().health} to ${c.source.asPlayer().maxHealth} by $sender"
                    )
                    it.health = it.maxHealth
                    c.source.asPlayer().sendMessage(
                        healedSuccessPlayerString.replace("{0}", player)
                    )
                    it.sendMessage(
                        healedSuccessPlayerRecipientString.replace("{0}", sender)
                    )
                }
            } else {
                if (c.source.asPlayer().health == c.source.asPlayer().maxHealth) {
                    c.source.asPlayer().sendMessage(healedFullErrorSelfString)
                    return
                }
                logger.info(
                    "Player ($sender) Health changed from ${c.source.asPlayer().health} to ${c.source.asPlayer().maxHealth}"
                )
                c.source.asPlayer().health = c.source.asPlayer().maxHealth
                c.source.asPlayer().sendMessage(healedSuccessSelfString)
            }
        }

        private val healedSuccessSelfString: String
            get() {
                return format(
                    "projectessentials.heal.self.success"
                )
            }
        private val healedSuccessPlayerString: String
            get() {
                return format(
                    "projectessentials.heal.player.success"
                )
            }
        private val healedSuccessPlayerRecipientString: String
            get() {
                return format(
                    "projectessentials.heal.player.recipient.success"
                )
            }
        private val healedFullErrorSelfString: String
            get() {
                return format(
                    "projectessentials.heal.self.maxhealth"
                )
            }
        private val healedFullErrorPlayerString: String
            get() {
                return format(
                    "projectessentials.heal.player.maxhealth"
                )
            }
        private val healErrorSelfString: String
            get() {
                return format(
                    "projectessentials.heal.self.error"
                )
            }
        private val healErrorPlayerString: String
            get() {
                return format(
                    "projectessentials.heal.player.error"
                )
            }
        private val errorPlayerNotOnlineString: String
            get() {
                return format(
                    "projectessentials.common.player.notonline"
                )
            }
    }
}
