package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.topLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.world.gen.Heightmap.Type

object TopCommand : CommandBase(topLiteral) {
    private const val topYPosModifier = 1.4
    private const val centerOfBlockPos = 0.5

    override val name = "top"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.top", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val position = context.getPlayer()!!.position
                val heightTop = context.getPlayer()!!.world
                    .getChunkAt(position)
                    .getTopBlockY(Type.MOTION_BLOCKING, position.x, position.z) + topYPosModifier

                BackLocationAPI.commit(context.getPlayer()!!)
                context.getPlayer()!!.setPositionAndUpdate(
                    position.x + centerOfBlockPos, heightTop, position.z + centerOfBlockPos
                )
                MessagingAPI.sendMessage(
                    context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.top.success"
                ).also { super.process(context) }
            }
        }
    }
}
