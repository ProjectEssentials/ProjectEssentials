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

object BurnCommand : CommandBase(burnLiteral, false) {
    override val name = "burn"
    override val aliases = listOf("fire")

    fun burnSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.burn.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.setFire(CommandAPI.getInt(context, "duration"))
                    MessagingAPI.sendMessage(
                        this, "${MESSAGE_MODULE_PREFIX}basic.burn.self.success"
                    )
                }.also { process(context) }
            }
        }
    }

    fun burnOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.burn.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            val duration = CommandAPI.getInt(context, "duration")
            players.forEach { player ->
                player.setFire(duration)
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.burn.by.success",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        "You've burned the ${players.first().name.string} player for $duration seconds"
                    } else {
                        "You've burned the selected (${players.count()}) players for $duration seconds"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        "${MESSAGE_MODULE_PREFIX}basic.burn.other_single.success"
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.burn.other_multiple.success"
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
