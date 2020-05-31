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

object RepairCommand : CommandBase(repairLiteral, false) {
    override val name = "repair"
    override val aliases = listOf("fix")

    fun repair(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.repair.one", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!.getHeldItem(Hand.MAIN_HAND)) {
                    if (!isDamaged) {
                        MessagingAPI.sendMessage(
                            context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.repair.error"
                        )
                        return@validateAndExecute
                    }
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.repair.success"
                    ).also { damage = 0 }.also { process(context) }
                }
            }
        }
    }

    fun repairAll(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.repair.all", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                var repairedItems = 0
                with(context.getPlayer()!!.inventory) {
                    listOf(mainInventory, armorInventory, offHandInventory).flatten().forEach {
                        if (it.isDamaged) {
                            it.damage = 0
                            repairedItems++
                        }
                    }
                }.also {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}basic.repair.all.success",
                        args = *arrayOf(repairedItems.toString())
                    ).also { process(context) }
                }
            }
        }
    }
}
