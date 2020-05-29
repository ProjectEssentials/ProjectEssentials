package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpToggleLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpToggleCommand : CommandBase(tpToggleLiteral) {
    override val name = "tp-toggle"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tptoggle", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                if (context.getPlayer()!!.name.string in TeleportManager.requestSuppressPlayers) {
                    TeleportManager.requestSuppressPlayers.remove(context.getPlayer()!!.name.string)
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tptoggle.off"
                    )
                } else {
                    TeleportManager.requestSuppressPlayers.add(context.getPlayer()!!.name.string)
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tptoggle.on"
                    )
                }
                super.process(context)
            }
        }
    }
}
