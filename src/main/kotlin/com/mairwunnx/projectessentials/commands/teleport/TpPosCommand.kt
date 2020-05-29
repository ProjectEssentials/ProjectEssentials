package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpPosLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.arguments.BlockPosArgument

object TpPosCommand : CommandBase(tpPosLiteral, false) {
    override val name = "tp-pos"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tppos", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val position = BlockPosArgument.getBlockPos(
                    context, "position"
                )
                with(context.getPlayer()!!) {
                    BackLocationAPI.commit(this)
                    this.teleport(
                        this.serverWorld,
                        position.x.toDouble() + 0.5,
                        position.y.toDouble(),
                        position.z.toDouble() + 0.5,
                        this.rotationYaw,
                        this.rotationPitch
                    )
                }
                MessagingAPI.sendMessage(
                    context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tppos.success"
                ).also { super.process(context) }
            }
        }
    }
}
