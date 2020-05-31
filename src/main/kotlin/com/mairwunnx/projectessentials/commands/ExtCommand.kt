package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playSound
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.SoundEvents.BLOCK_SMOKER_SMOKE

object ExtCommand : CommandBase(extLiteral, false) {
    override val name = "ext"
    override val aliases = listOf("extinguish")

    fun extSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.ext.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    this.extinguish()
                    this.playSound(this, BLOCK_SMOKER_SMOKE)
                    MessagingAPI.sendMessage(
                        this, "${MESSAGE_MODULE_PREFIX}basic.ext.self.success"
                    )
                }.also { process(context) }
            }
        }
    }

    fun extOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.ext.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                player.extinguish()
                player.playSound(player, BLOCK_SMOKER_SMOKE)
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.ext.by.success",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        "You've extinguished player ${players.first().name.string}."
                    } else {
                        "You've extinguished selected (${players.count()}) players."
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        "${MESSAGE_MODULE_PREFIX}basic.ext.other_single.success"
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.ext.other_multiple.success"
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
