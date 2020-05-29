package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpaCancelLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.managers.TeleportRemoveRequestResponse
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpaCancelCommand : CommandBase(tpaCancelLiteral) {
    override val name = "tpa-cancel"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpacancel", 0) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val result = TeleportManager.removeLastRequest(context.getPlayer()!!.name.string)
                if (result == TeleportRemoveRequestResponse.NothingToRemove) {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpacencel.error"
                    )
                    return@validateAndExecute
                }
                MessagingAPI.sendMessage(
                    context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpacencel.success"
                ).also { super.process(context) }
            }
        }
    }
}
