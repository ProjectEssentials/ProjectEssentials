package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpaLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.TeleportManager
import com.mairwunnx.projectessentials.managers.TeleportRequestResponse.*
import com.mairwunnx.projectessentials.managers.TeleportRequestType
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpaCommand : CommandBase(tpaLiteral, false) {
    override val name = "tpa"
    override val aliases = listOf("call")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpa", 0) { isServer ->
            fun out(result: String) = MessagingAPI.sendMessage(
                context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpa.$result",
                args = *arrayOf(CommandAPI.getPlayer(context, "target").name.string)
            ).also {
                if (result == "success") {
                    MessagingAPI.sendMessage(
                        CommandAPI.getPlayer(context, "target"),
                        "${MESSAGE_MODULE_PREFIX}basic.tpa.by",
                        args = *arrayOf(context.playerName())
                    )
                }
            }

            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(
                    TeleportManager.makeRequest(
                        TeleportRequestType.To,
                        context.getPlayer()!!.name.string,
                        CommandAPI.getPlayer(context, "target").name.string
                    )
                ) {
                    when (this) {
                        RequestToSuccess -> out("success")
                        RequestedIsIgnored -> out("requested_ignored")
                        SuchRequestExist -> out("request_exist")
                        RequestFromSuccess -> IllegalStateException()
                    }
                }
                super.process(context)
            }
        }
    }
}
