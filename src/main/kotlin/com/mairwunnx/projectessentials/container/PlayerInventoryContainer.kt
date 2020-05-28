package com.mairwunnx.projectessentials.container

import com.mairwunnx.projectessentials.SETTING_INVSEE_DISABLE_DANGER_SLOTS
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.ItemStack

class PlayerInventoryContainer(
    type: ContainerType<*>,
    id: Int,
    playerInventory: PlayerInventory,
    inventoryToOpen: IInventory,
    rows: Int
) : ChestContainer(type, id, playerInventory, inventoryToOpen, rows) {
    override fun slotClick(
        slotId: Int, dragType: Int, clickTypeIn: ClickType, player: PlayerEntity
    ): ItemStack {
        if (generalConfiguration.getBool(SETTING_INVSEE_DISABLE_DANGER_SLOTS)) {
            if (slotId in 41..44) return ItemStack.EMPTY
        }
        if (player is ServerPlayerEntity && !hasPermission(player, "ess.inventory.edit", 4)) {
            return ItemStack.EMPTY
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    override fun canInteractWith(playerIn: PlayerEntity) = playerIn.isAlive

    companion object {
        private val generalConfiguration by lazy {
            getConfigurationByName<GeneralConfiguration>("general")
        }
    }
}
