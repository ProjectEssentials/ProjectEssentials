package com.mairwunnx.projectessentials.commands.moderator

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager
import kotlin.math.roundToInt

object GetPosCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.getPos

    init {
        command = "getpos"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.getPos
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
            return if (targetIsExists) {
                val posX = targetPlayer.positionVec.x.roundToInt()
                val posY = targetPlayer.positionVec.y.roundToInt()
                val posZ = targetPlayer.positionVec.z.roundToInt()
                logger.info("Player $targetName current position XYZ: $posX / $posY / $posZ")
                0
            } else {
                throwOnlyPlayerCan(command)
                0
            }
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(senderName, "ess.getpos.other")) {
                    when {
                        !config.enableArgs -> {
                            logger.warn(
                                DISABLED_COMMAND_ARG
                                    .replace("%0", senderName)
                                    .replace("%1", command)
                            )
                            sendMsg(sender, "common.arg.disabled", command)
                            return 0
                        }
                    }

                    val posX = targetPlayer.positionVec.x.roundToInt()
                    val posY = targetPlayer.positionVec.y.roundToInt()
                    val posZ = targetPlayer.positionVec.z.roundToInt()
                    sendMsg(
                        sender, "getpos.other.out", targetName,
                        posX.toString(), posY.toString(), posZ.toString()
                    )
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "getpos.other.restricted", targetName)
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.getpos")) {
                    sendMsg(
                        sender,
                        "getpos.self.out",
                        senderPlayer.positionVec.x.roundToInt().toString(),
                        senderPlayer.positionVec.y.roundToInt().toString(),
                        senderPlayer.positionVec.z.roundToInt().toString()
                    )
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "getpos.self.restricted", senderName)
                    return 0
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
