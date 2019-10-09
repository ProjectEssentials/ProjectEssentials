package com.mairwunnx.projectessentials.commands.health

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

object AirCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.air

    init {
        command = "air"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.air
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        "player", EntityArgument.player()
                    ).executes {
                        return@executes execute(it, true)
                    }
                )
                .executes {
                    return@executes execute(it)
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)
        if (senderIsServer) {
            if (targetIsExists) {
                if (targetPlayer.air == targetPlayer.maxAir) {
                    logger.info("Player $targetName have a full supply of air.")
                    return 0
                }
                logger.info(
                    "Player ($targetName) air level changed from ${targetPlayer.air} to ${targetPlayer.maxAir} by $senderName"
                )
                targetPlayer.air = targetPlayer.maxAir
                logger.info("You saved player $targetName from choking.")
                sendMsg(
                    targetPlayer.commandSource,
                    "air.other.recipient_out",
                    senderName
                )
            } else {
                logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            }
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(senderName, "ess.air.other")) {
                    if (targetPlayer.air == targetPlayer.maxAir) {
                        sendMsg(sender, "air.other.maxair", targetName)
                        return 0
                    }
                    logger.info(
                        "Player ($targetName) air level changed from ${targetPlayer.air} to ${targetPlayer.maxAir} by $senderName"
                    )
                    targetPlayer.air = targetPlayer.maxAir
                    sendMsg(sender, "air.other.success", targetName)
                    sendMsg(
                        targetPlayer.commandSource,
                        "air.other.recipient_out",
                        senderName
                    )
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "air.other.restricted", senderName)
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.air")) {
                    if (senderPlayer.air == senderPlayer.maxAir) {
                        sendMsg(sender, "air.self.maxair")
                        return 0
                    }
                    logger.info(
                        "Player ($senderName) air level changed from ${senderPlayer.air} to ${senderPlayer.maxAir}"
                    )
                    senderPlayer.air = senderPlayer.maxAir
                    sendMsg(sender, "air.self.success")
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "air.self.restricted", senderName)
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
