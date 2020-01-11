package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.ProjectEssentials
import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

object TpAcceptCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpAccept

    init {
        command = "tpaccept"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpAccept
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
            logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.tpaccept")) {
                val requestInitiator =
                    ProjectEssentials.teleportPresenter.getRequest(senderPlayer)

                if (requestInitiator == null) {
                    sendMsg(sender, "tpaccept.not_exist_requests")
                } else {
                    requestInitiator.teleport(
                        senderPlayer.serverWorld,
                        senderPlayer.posX,
                        senderPlayer.posY,
                        senderPlayer.posZ,
                        senderPlayer.rotationYaw,
                        senderPlayer.rotationPitch
                    )
                    sendMsg(
                        requestInitiator.commandSource, "tpaccept.request_accepted"
                    )
                    ProjectEssentials.teleportPresenter.removeRequest(
                        requestInitiator,
                        senderPlayer
                    )
                }
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "tpaccept.restricted")
                return 0
            }
        }
        logger.info("Executed command \"/${command}\" from $senderName")
        return 0
    }
}
