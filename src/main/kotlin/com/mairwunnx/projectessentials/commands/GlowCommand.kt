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

object GlowCommand : CommandBase(glowLiteral, false) {
    override val name = "glow"

    fun glowSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.glow.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.isGlowing = !this.isGlowing
                    MessagingAPI.sendMessage(
                        this,
                        "${MESSAGE_MODULE_PREFIX}basic.glow.self.${if (this.isGlowing) "enabled" else "disabled"}"
                    )
                }.also { process(context) }
            }
        }
    }

    fun glowOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.glow.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                player.isGlowing = !player.isGlowing
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.glow.by.${if (player.isGlowing) "enabled" else "disabled"}",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        if (players.first().isGlowing) {
                            "You've enabled glowing for player ${players.first().name.string}."
                        } else {
                            "You've disabled glowing for player ${players.first().name.string}."
                        }
                    } else {
                        "You've switched glowing for selected (${players.count()}) players."
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        if (players.first().isGlowing) {
                            "${MESSAGE_MODULE_PREFIX}basic.glow.other_single.enabled"
                        } else {
                            "${MESSAGE_MODULE_PREFIX}basic.glow.other_single.disabled"
                        }
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.glow.other_multiple.success"
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
