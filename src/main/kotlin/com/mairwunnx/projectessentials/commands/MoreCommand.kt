package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.Hand

object MoreCommand : CommandBase(moreLiteral) {
    override val name = "more"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.more", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val item = context.getPlayer()!!.getHeldItem(Hand.MAIN_HAND)
                if (item.count < item.maxStackSize) {
                    item.count = item.maxStackSize
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.more.success"
                    ).also { super.process(context) }
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.more.fullstack"
                    )
                }
            }
        }
    }
}
