package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraftforge.fml.server.ServerLifecycleHooks

object PingCommand : CommandBase(pingLiteral) {
    override val name = "ping"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.ping", 0) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    "${MESSAGE_MODULE_PREFIX}basic.ping.success",
                    args = *arrayOf(
                        ServerLifecycleHooks.getCurrentServer().serverHostname,
                        context.getPlayer()!!.ping.toString()
                    )
                ).also { super.process(context) }
            }
        }
    }
}
