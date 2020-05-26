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

object AirCommand : CommandBase(airLiteral, false) {
    override val name = "air"

    fun airSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.air.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.air = this.maxAir
                    MessagingAPI.sendMessage(
                        this, "${MESSAGE_MODULE_PREFIX}basic.air.self.success"
                    )
                }.also { process(context) }
            }
        }
    }

    fun airOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.air.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                player.air = player.maxAir
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.air.by.success",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        "You've replenished the ${players.first().name.string}'s air supply"
                    } else {
                        "You've replenished the selected (${players.count()}) players's air supply"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        "${MESSAGE_MODULE_PREFIX}basic.air.other_single.success"
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.air.other_multiple.success"
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
