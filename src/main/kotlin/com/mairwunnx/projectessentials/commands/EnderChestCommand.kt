package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.EnderChestBlock
import net.minecraft.command.CommandSource
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.IContainerProvider
import net.minecraft.inventory.container.SimpleNamedContainerProvider

object EnderChestCommand : CommandBase(enderChestLiteral, false) {
    override val name = "enderchest"
    override val aliases = listOf("ender-chest", "ec")

    fun openSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.enderchest.open", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                context.getPlayer()!!.openContainer(
                    SimpleNamedContainerProvider(
                        IContainerProvider { id, inventory, _ ->
                            ChestContainer.createGeneric9X3(
                                id, inventory, context.getPlayer()!!.inventoryEnderChest
                            )
                        }, EnderChestBlock.field_220115_d
                    )
                )
            }
        }
    }

    fun openOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.enderchest.open.other", 4) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val target = CommandAPI.getPlayer(context, "target")
                context.getPlayer()!!.openContainer(
                    SimpleNamedContainerProvider(
                        IContainerProvider { id, inventory, _ ->
                            ChestContainer.createGeneric9X3(
                                id, inventory, target.inventoryEnderChest
                            )
                        }, EnderChestBlock.field_220115_d
                    )
                )
            }
        }
    }
}
