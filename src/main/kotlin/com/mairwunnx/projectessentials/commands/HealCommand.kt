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

object HealCommand : CommandBase(healLiteral, false) {
    override val name = "heal"

    fun healSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.heal.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.health = this.maxHealth
                    MessagingAPI.sendMessage(
                        this, "${MESSAGE_MODULE_PREFIX}basic.heal.self.success"
                    )
                }.also { process(context) }
            }
        }
    }

    fun healOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.heal.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                player.health = player.maxHealth
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.heal.by.success",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        "You've healed player ${players.first().name.string}"
                    } else {
                        "You've healed the selected (${players.count()}) players"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        "${MESSAGE_MODULE_PREFIX}basic.heal.other_single.success"
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.heal.other_multiple.success"
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
