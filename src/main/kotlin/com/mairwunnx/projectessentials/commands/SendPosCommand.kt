package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object SendPosCommand : CommandBase(sendPosLiteral) {
    override val name = "sendpos"
    override val aliases = listOf("send-pos", "share-positions")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.sendpos", 0) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val sender = context.getPlayer()!!
                CommandAPI.getPlayers(context, "targets").forEach { player ->
                    MessagingAPI.sendMessage(
                        player, "${MESSAGE_MODULE_PREFIX}basic.sendpos.receiver.success",
                        args = *arrayOf(
                            sender.name.string,
                            sender.currentDimensionName,
                            sender.positionVec.x.toInt().toString(),
                            sender.positionVec.y.toInt().toString(),
                            sender.positionVec.z.toInt().toString()
                        )
                    ).also { super.process(context) }
                }
            }
        }
    }
}
