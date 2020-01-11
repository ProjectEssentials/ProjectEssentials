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
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

object TpaCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpa

    init {
        command = "tpa"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpa
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument("player", EntityArgument.player()).executes {
                        return@executes execute(it, true)
                    }
                )
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
            if (PermissionsAPI.hasPermission(senderName, "ess.tpa")) {
                if (ProjectEssentials.teleportPresenter.commitRequest(senderPlayer, targetPlayer)) {
                    sendMsg(sender, "request success")
                    sendMsg(target, "request from $senderName")
                } else {
                    sendMsg(sender, "request already exists or player restricted")
                    sendMsg(target, "$senderName tried to teleport to you")
                }
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "tpa.restricted")
                return 0
            }
        }
        logger.info("Executed command \"/${command}\" from $senderName")
        return 0
    }
}
