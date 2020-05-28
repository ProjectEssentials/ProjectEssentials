package com.mairwunnx.projectessentials.container

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.WorkbenchContainer
import net.minecraft.util.IWorldPosCallable

class VirtualWorkbenchContainer(
    id: Int, playerInventory: PlayerInventory, sourcePos: IWorldPosCallable
) : WorkbenchContainer(id, playerInventory, sourcePos) {
    override fun canInteractWith(playerIn: PlayerEntity) = true
}
