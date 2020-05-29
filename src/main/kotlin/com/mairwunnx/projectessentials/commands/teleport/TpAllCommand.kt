package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.tpAllLiteral
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

object TpAllCommand : CommandBase(tpAllLiteral, false) {
    override val name = "tp-all"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.teleport.tpall", 4) { isServer ->
            with(CommandAPI.getPlayer(context, "target")) {
                context.source.server.playerList.players.forEach {
                    BackLocationAPI.commit(it)
                    it.teleport(
                        this.serverWorld,
                        this.positionVec.x, this.positionVec.y, this.positionVec.z,
                        this.rotationYaw, this.rotationPitch
                    )
                }
            }.also {
                if (isServer) {
                    ServerMessagingAPI.response { "Teleporting all players to target ..." }
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.tpall.success"
                    ).also { super.process(context) }
                }
            }
        }
    }
}
