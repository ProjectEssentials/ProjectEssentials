package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpDenyLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportAcceptRequestResponse.*
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpDenyCommand : CommandBase(tpDenyLiteral) {
    override val name = "tp-deny"
    override val aliases = listOf("tp-no")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpdeny", 0) { isServer ->
            fun out(status: String) = MessagingAPI.sendMessage(
                context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpdeny.${status}"
            )

            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val result = TeleportManager.takeRequest(context.getPlayer()!!.name.string)
                when (result.first) {
                    NothingToAccept -> out("nothing_cancel")
                    RequestedPlayerOffline, AcceptedToSuccessful, AcceptedHereSuccessful -> {
                        out("success").also { super.process(context) }.also {
                            result.second?.let {
                                MessagingAPI.sendMessage(
                                    it, "${MESSAGE_MODULE_PREFIX}basic.tpdeny.by.denied"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
