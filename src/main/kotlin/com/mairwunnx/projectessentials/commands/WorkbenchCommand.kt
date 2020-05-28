package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.container.VirtualWorkbenchContainer
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.inventory.container.IContainerProvider
import net.minecraft.inventory.container.SimpleNamedContainerProvider
import net.minecraft.util.IWorldPosCallable
import net.minecraft.util.text.TranslationTextComponent

object WorkbenchCommand : CommandBase(workbenchLiteral) {
    override val name = "workbench"
    override val aliases = listOf("wb", "craft")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.workbench.open", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val player = context.getPlayer()!!
                player.openContainer(
                    SimpleNamedContainerProvider(
                        IContainerProvider { id, playerInventory, _ ->
                            VirtualWorkbenchContainer(
                                id, playerInventory, IWorldPosCallable.of(
                                    player.world, player.position
                                )
                            )
                        }, TranslationTextComponent("container.crafting")
                    )
                ).also { super.process(context) }
            }
        }
    }
}
