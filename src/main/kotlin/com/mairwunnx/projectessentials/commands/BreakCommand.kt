package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.material.Material
import net.minecraft.command.CommandSource
import net.minecraft.util.math.BlockPos

object BreakCommand : CommandBase(breakLiteral) {
    override val name = "break"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.break", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val player = context.getPlayer()!!
                val eyePosition = player.getEyePosition(1.0F)
                val lookVector = player.getLook(1.0F)
                for (i in 1..26) {
                    val vector = eyePosition.add(
                        lookVector.x * i, lookVector.y * i, lookVector.z * i
                    )
                    val blockPos = BlockPos(vector.x, vector.y, vector.z)
                    if (player.world.getBlockState(blockPos).material != Material.AIR) {
                        player.world.destroyBlock(blockPos, false)
                        break
                    }
                }
                super.process(context)
            }
        }
    }
}
