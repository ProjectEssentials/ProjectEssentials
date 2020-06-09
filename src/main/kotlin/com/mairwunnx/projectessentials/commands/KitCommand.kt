package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.UserDataConfiguration
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
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

    private val userDataConfiguration by lazy {
        getConfigurationByName<UserDataConfiguration>("user-data")
    }

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

                when (KitManager.requestKit(player, kit)) {
                    Response.Success -> out("success")
                    Response.KitNoHasPermissions -> out("no_permission")
                    Response.KitNotFound -> out("not_found")
                    Response.KitTimeNotExpired -> out("kit_not_expired")
                }
            }
        }
    }

    fun kitRefreshCooldown(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.kit.receive.other.cooldown.refresh", 4) { isServer ->
            val kit = CommandAPI.getString(context, "kit-name")
            val target = CommandAPI.getPlayer(context, "target")
            val targetName = target.name.string

            fun refreshFailed() {
                if (isServer) {
                    ServerMessagingAPI.response { "Player $targetName has no cooldown for kit $kit." }
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}basic.kit.other.cooldown_refresh.error",
                        args = *arrayOf(targetName, kit)
                    )
                }
            }

            userDataConfiguration.take().users.find {
                it.name == targetName || it.uuid == target.uniqueID.toString()
            }?.let { user ->
                user.lastKitsDates.map { value ->
                    value.split('@').let { it[0] to it[1] }
                }.find { it.first == kit }?.let { expiredKit ->
                    user.lastKitsDates.removeAll { expiredKit.first in it }.also {
                        if (it) {
                            if (isServer) {
                                ServerMessagingAPI.response { "Kit $kit cooldown was removed for player $targetName." }
                            } else {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}basic.kit.other.cooldown_refresh.success",
                                    args = *arrayOf(kit, targetName)
                                )
                            }
                        } else refreshFailed()
                    }
                } ?: run { refreshFailed() }
            } ?: run { refreshFailed() }
        }
    }
}
