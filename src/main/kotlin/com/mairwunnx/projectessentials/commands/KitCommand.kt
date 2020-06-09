package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.managers.KitManager
import com.mairwunnx.projectessentials.managers.KitManager.Response
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object KitCommand : CommandBase(kitLiteral, false) {
    override val name = "kit"

    fun kitList(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.list", 0) { isServer ->
            val list = KitManager.getKits().map { it.name }.toList()
            if (isServer) {
                ServerMessagingAPI.listAsResponse(list) { "Kits list" }
            } else {
                MessagingAPI.sendListAsMessage(context, list) { "Kits list" }
                process(context)
            }
        }
    }

    fun kitGet(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.receive", 0) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val kit = CommandAPI.getString(context, "kit-name")
                val player = context.getPlayer()!!

                fun out(result: String) {
                    MessagingAPI.sendMessage(
                        player,
                        "${MESSAGE_MODULE_PREFIX}basic.kit.self.$result",
                        args = *arrayOf(kit)
                    ).also { if (result == "success") process(context) }
                }

                when (KitManager.requestKit(player, player, kit)) {
                    Response.Success -> out("success")
                    Response.KitNoHasPermissions -> out("no_permission")
                    Response.KitNotFound -> out("not_found")
                    Response.KitTimeNotExpired -> out("kit_not_expired")
                }
            }
        }
    }

    fun kitGetOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.receive.other", 3) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                val kit = CommandAPI.getString(context, "kit-name")
                val sender = context.getPlayer()!!
                val target = CommandAPI.getPlayer(context, "target")

                fun out(result: String, vararg args: String) {
                    MessagingAPI.sendMessage(
                        sender,
                        "${MESSAGE_MODULE_PREFIX}basic.kit.other.$result",
                        args = *args
                    ).also {
                        if (result == "success") {
                            MessagingAPI.sendMessage(
                                target,
                                "${MESSAGE_MODULE_PREFIX}basic.kit.by",
                                args = *arrayOf(sender.name.string)
                            ).also { process(context) }
                        }
                    }
                }

                when (KitManager.requestKit(sender, target, kit)) {
                    Response.Success -> out("success", kit, target.name.string)
                    Response.KitNoHasPermissions -> out("no_permission", kit)
                    Response.KitNotFound -> out("not_found", kit)
                    Response.KitTimeNotExpired -> out("kit_not_expired", kit)
                }
            }
        }
    }
}
