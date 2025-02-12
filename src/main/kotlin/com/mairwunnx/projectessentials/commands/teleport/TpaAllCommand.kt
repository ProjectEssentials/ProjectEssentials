package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpaAllLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.managers.TeleportRequestAllResponse
import com.mairwunnx.projectessentials.managers.TeleportRequestType
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpaAllCommand : CommandBase(tpaAllLiteral) {
    override val name = "tpa-all"
    override val aliases = listOf("call-all")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpaall", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                TeleportManager.makeRequestToAll(
                    TeleportRequestType.To, context.getPlayer()!!.name.string
                ).also {
                    if (it == TeleportRequestAllResponse.Success) {
                        MessagingAPI.sendMessage(
                            context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpaall.success"
                        ).also { super.process(context) }
                    } else {
                        MessagingAPI.sendMessage(
                            context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpaall.error"
                        )
                    }
                }
            }
        }
    }
}
