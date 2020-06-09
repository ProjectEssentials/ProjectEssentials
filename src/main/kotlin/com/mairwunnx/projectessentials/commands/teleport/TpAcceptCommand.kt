package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpAcceptLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportAcceptRequestResponse
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

object TpAcceptCommand : CommandBase(tpAcceptLiteral) {
    override val name = "tp-accept"
    override val aliases = listOf("tp-yes")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpaccept", 0) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val result = TeleportManager.takeRequest(context.getPlayer()!!.name.string)
                val response = result.first
                val initiator = result.second

                fun out(status: String) = MessagingAPI.sendMessage(
                    context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpaccept.${status}"
                )

                fun outTarget(status: String) = MessagingAPI.sendMessage(
                    initiator!!, "${MESSAGE_MODULE_PREFIX}basic.tpaccept.by.${status}",
                    args = *arrayOf(context.playerName())
                )

                when (response) {
                    TeleportAcceptRequestResponse.NothingToAccept -> out("nothing_accept")
                    TeleportAcceptRequestResponse.RequestedPlayerOffline -> out("player_offline")
                    TeleportAcceptRequestResponse.AcceptedToSuccessful -> {
                        teleport(initiator!!, context.getPlayer()!!)
                        out("to.success")
                            .also { outTarget("to.success") }
                            .also { super.process(context) }
                    }
                    TeleportAcceptRequestResponse.AcceptedHereSuccessful -> {
                        teleport(context.getPlayer()!!, initiator!!)
                        out("from.success")
                            .also { outTarget("from.success") }
                            .also { super.process(context) }
                    }
                }
            }
        }
    }

    private fun teleport(from: ServerPlayerEntity, to: ServerPlayerEntity) {
        from.teleport(
            to.serverWorld,
            to.positionVec.x, to.positionVec.y, to.positionVec.z,
            to.rotationYaw, to.rotationPitch
        )
    }
}
