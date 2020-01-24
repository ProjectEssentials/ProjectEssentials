package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.extensions.dimName
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import kotlin.math.roundToInt

object SendPosCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.sendPos

    init {
        command = "sendpos"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.sendPos
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
                        return@executes execute(
                            it, EntityArgument.getPlayer(it, "player")
                        )
                    }
                )
                .then(
                    Commands.argument(
                        "players", EntityArgument.players()
                    ).executes {
                        return@executes execute(
                            it, EntityArgument.getPlayers(it, "players")
                        )
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
            if (!PermissionsAPI.hasPermission(senderName, "ess.sendpos")) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "sendpos.restricted")
                return 0
            }

            if (argument is ServerPlayerEntity) {
                sendMsg(
                    argument.commandSource,
                    "sendpos.pattern",
                    senderName,
                    senderPlayer.serverWorld.dimName(),
                    senderPlayer.posX.roundToInt().toString(),
                    senderPlayer.posY.roundToInt().toString(),
                    senderPlayer.posZ.roundToInt().toString()
                )
            } else {
                @Suppress("UNCHECKED_CAST")
                val players = argument as Collection<ServerPlayerEntity>
                players.forEach {
                    sendMsg(
                        it.commandSource,
                        "sendpos.pattern",
                        senderName,
                        senderPlayer.serverWorld.dimName(),
                        senderPlayer.posX.roundToInt().toString(),
                        senderPlayer.posY.roundToInt().toString(),
                        senderPlayer.posZ.roundToInt().toString()
                    )
                }
            }
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
