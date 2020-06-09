package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpHereLiteral
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object TpHereCommand : CommandBase(tpHereLiteral, false) {
    override val name = "tp-here"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tphere", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val target = CommandAPI.getPlayer(context, "target")
                with(context.getPlayer()!!) {
                    BackLocationAPI.commit(target)
                    target.teleport(
                        this.serverWorld,
                        this.positionVec.x, this.positionVec.y, this.positionVec.z,
                        this.rotationYaw, this.rotationPitch
                    )
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}basic.tphere.success",
                        args = *arrayOf(target.name.string)
                    ).also { super.process(context) }
                }
            }
        }
    }
}
