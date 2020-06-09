package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.container.PlayerInventoryContainer
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.IContainerProvider
import net.minecraft.inventory.container.SimpleNamedContainerProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentUtils

object InvSeeCommand : CommandBase(invSeeLiteral, false) {
    override val name = "invsee"
    override val aliases = listOf("inventory-see")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.inventory.see", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val target = CommandAPI.getPlayer(context, "target")
                if (hasPermission(target, "ess.inventory.see.exempt", 4)) {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.invsee.error"
                    )
                } else {
                    val field = target.inventory.javaClass.getDeclaredField("field_184439_c")
                    field.isAccessible = true
                    field.set(target.inventory, NonNullList.withSize(5, ItemStack.EMPTY))
                    context.getPlayer()!!.openContainer(
                        SimpleNamedContainerProvider(
                            IContainerProvider { id, playerInventory, _ ->
                                PlayerInventoryContainer(
                                    type = ContainerType.GENERIC_9X5,
                                    id = id,
                                    playerInventory = playerInventory,
                                    inventoryToOpen = target.inventory,
                                    rows = 5
                                )
                            }, TextComponentUtils.toTextComponent {
                                "§c${target.name.string}'s §7inventory"
                            }
                        )
                    )
                    field.set(target.inventory, NonNullList.withSize(1, ItemStack.EMPTY))
                    super.process(context)
                }
            }
        }
    }
}
