package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.AfkManager
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object AfkCommand : CommandBase(afkLiteral, false) {
    override val name = "afk"
    override val aliases = listOf("away")

    fun afkList(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.afk.set", 0) { isServer ->
            val list = AfkManager.getAfkPlayers().map { it.name.string }
            if (isServer) {
                ServerMessagingAPI.listAsResponse(list) { "Afk Players" }
            } else {
                MessagingAPI.sendListAsMessage(context, list) { "Afk Players" }
                process(context)
            }
        }
    }

    fun afkSet(context: CommandContext<CommandSource>): Int = 0.also {
        validateAndExecute(context, "ess.afk.list", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                AfkManager.switch(
                    context.getPlayer()!!, context.getPlayer()!! !in AfkManager.getAfkPlayers()
                ).also { process(context) }
            }
        }
    }
}
