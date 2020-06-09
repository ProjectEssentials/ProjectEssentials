package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT

object SkullCommand : CommandBase(skullLiteral, false) {
    override val name = "skull"
    override val aliases = listOf("head")

    fun skull(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.skull", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                ItemStack(Items.PLAYER_HEAD).also { process(context) }.apply {
                    setTagInfo("SkullOwner", CompoundNBT())
                    tag?.putString("SkullOwner", CommandAPI.getString(context, "nick"))
                }.also { context.getPlayer()!!.addItemStackToInventory(it) }
            }
        }
    }
}
