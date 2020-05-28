package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.KitManager
import com.mairwunnx.projectessentials.managers.KitManager.Response
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

object KitCommand : CommandBase(kitLiteral, false) {
    override val name = "kit"

    fun kitList(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.list", 0) { isServer ->
            val list = KitManager.getKits().map { it.name }
            if (isServer) {
                ServerMessagingAPI.listAsResponse(list) { "Kits list" }
            } else {
                MessagingAPI.sendListAsMessage(context, list) { "Kits list" }
                process(context)
            }
        }
    }

    fun kitGet(context: CommandContext<CommandSource>) = 0.also {
        if (context.isPlayerSender()) {
            val kit = CommandAPI.getString(context, "kit-name")
            val player = context.getPlayer()!!
            when (KitManager.requestKit(player, kit)) {
                Response.Success -> kitSelfOut(player, kit, "success").also { process(context) }
                Response.KitNoHasPermissions -> kitSelfOut(player, kit, "no_permission")
                Response.KitNotFound -> kitSelfOut(player, kit, "not_found")
                Response.KitTimeNotExpired -> kitSelfOut(player, kit, "kit_not_expired")
            }
        } else {
            ServerMessagingAPI.throwOnlyPlayerCan()
        }
    }

    private fun kitSelfOut(player: ServerPlayerEntity, kit: String, result: String) {
        MessagingAPI.sendMessage(
            player, "${MESSAGE_MODULE_PREFIX}basic.kit.self.$result", args = *arrayOf(kit)
        )
    }
}
