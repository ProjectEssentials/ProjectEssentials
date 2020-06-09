package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.world.server.ServerWorld

object LightningCommand : CommandBase(lightningLiteral) {
    override val name = "lightning"
    override val aliases = listOf("thor")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.lightning", 3) { isServer ->
            val entities = CommandAPI.getEntities(context, "targets")
            entities.forEach { entity ->
                val lightning = LightningBoltEntity(
                    entity.world,
                    entity.positionVec.x, entity.positionVec.y, entity.positionVec.z,
                    true
                )
                (entity.world as ServerWorld).addLightningBolt(lightning)
                entity.onStruckByLightning(lightning)
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    "You've smiting by lightning strike the selected (${entities.count()}) entities"
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    "${MESSAGE_MODULE_PREFIX}basic.lightning.success",
                    args = *arrayOf(entities.count().toString())
                ).also { super.process(context) }
            }
        }
    }
}
