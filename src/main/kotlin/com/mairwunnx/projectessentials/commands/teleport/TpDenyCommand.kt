package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.ProjectEssentials.Companion.teleportPresenter
import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

object TpDenyCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpDeny

    init {
        command = "tpdeny"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpDeny
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes { execute(it) }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)

        if (senderIsServer) {
            throwOnlyPlayerCan(command)
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.tpdeny")) {
                val requestInitiator =
                    teleportPresenter.getRequest(senderPlayer)

                val requestHereInitiator =
                    teleportPresenter.getRequestHere(senderPlayer)

                when {
                    requestInitiator != null -> {
                        if (teleportPresenter.removeRequest(
                                requestInitiator.name.string, senderPlayer.name.string
                            )
                        ) {
                            sendMsg(
                                requestInitiator.commandSource,
                                "tpdeny.request_denied",
                                senderName
                            )
                            sendMsg(
                                sender,
                                "tpdeny.request_denied_successfully",
                                requestInitiator.name.string
                            )
                        }
                    }
                    requestHereInitiator != null -> {
                        if (teleportPresenter.removeRequestHere(
                                requestHereInitiator.name.string, senderPlayer.name.string
                            )
                        ) {
                            sendMsg(
                                requestHereInitiator.commandSource,
                                "tpdeny.request_denied",
                                senderName
                            )
                            sendMsg(
                                sender,
                                "tpdeny.request_denied_successfully",
                                requestHereInitiator.name.string
                            )
                        }
                    }
                    else -> {
                        sendMsg(sender, "tpdeny.nothing_to_cancel")
                    }
                }
            } else {
                throwPermissionLevel(senderName, command)
                sendMsg(sender, "tpdeny.restricted")
                return 0
            }
        }
        logger.info("Executed command \"/${command}\" from $senderName")
        return 0
    }
}
