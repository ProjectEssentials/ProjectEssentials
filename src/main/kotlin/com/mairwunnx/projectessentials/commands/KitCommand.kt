package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.KitManager
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object KitCommand : CommandBase(kitLiteral, false) {
    override val name = "kit"

    fun kitList(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.list", 0) { isServer ->
            val list = KitManager.getKits().map { it.name }
            if (isServer) {
                ServerMessagingAPI.listAsResponse(list) { "Kits list" }
            } else {
                MessagingAPI.sendListAsMessage(context, list) { "Kits list" }
                process(context)
            }
        }
    }

    fun airSelf(context: CommandContext<CommandSource>) = 0.also {

    }
}
