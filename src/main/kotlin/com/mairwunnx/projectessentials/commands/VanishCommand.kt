package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object VanishCommand : CommandBase(vanishLiteral, false) {
    override val name = "vanish"
    override val aliases = listOf("v", "invisible")

    fun vanishSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.vanish.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.isInvisible = !this.isInvisible
                    MessagingAPI.sendMessage(
                        this,
                        "${MESSAGE_MODULE_PREFIX}basic.vanish.self.${if (this.isInvisible) "enabled" else "disabled"}"
                    )
                }.also { process(context) }
            }
        }
    }

    fun vanishOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.vanish.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                player.isInvisible = !player.isInvisible
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.vanish.by.${if (player.isInvisible) "enabled" else "disabled"}",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        if (players.first().isInvisible) {
                            "You've enabled invisible for player ${players.first().name.string}."
                        } else {
                            "You've disabled invisible for player ${players.first().name.string}."
                        }
                    } else {
                        "You've switched invisible for selected (${players.count()}) players."
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        if (players.first().isInvisible) {
                            "${MESSAGE_MODULE_PREFIX}basic.vanish.other_single.enabled"
                        } else {
                            "${MESSAGE_MODULE_PREFIX}basic.vanish.other_single.disabled"
                        }
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.vanish.other_multiple.success"
                    },
                    args = *arrayOf(
                        if (players.count() == 1) {
                            players.first().name.string
                        } else {
                            players.count().toString()
                        }
                    )
                ).also { process(context) }
            }
        }
    }
}
