package com.mairwunnx.projectessentials.commands.moderator

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.extensions.playerName
import com.mairwunnx.projectessentialscore.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

// todo: add registerNative method for overriding ClearCommand from minecraft.

object ClearCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.clear

    init {
        command = "clear"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.clear
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        aliases.forEach { command ->
            dispatcher.register(
                literal<CommandSource>(command).executes {
                    execute(it)
                }.then(
                    Commands.argument(
                        "target", EntityArgument.player()
                    ).executes {
                        // argument is true because we have EntityArgument.player.
                        execute(it, true)
                    }
                ).then(
                    Commands.argument(
                        "targets", EntityArgument.players()
                    ).executes {
                        // this we use any argument for detecting EntityArgument.players()
                        // because argument is true for EntityArgument.player().
                        execute(it, "targets")
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
            if (targetIsExists) {
                var counter = 0
                val players: MutableList<ServerPlayerEntity> = mutableListOf()

                if (argument is String && argument == "targets") {
                    EntityArgument.getPlayers(c, "targets").forEach {
                        players.add(it)
                    }
                } else {
                    players.add(targetPlayer)
                }

                players.forEach {
                    counter += it.inventory.clearMatchingItems({ true }, -1)
                    it.openContainer.detectAndSendChanges()
                    it.updateHeldItem()
                    sendMsg(it.commandSource, "clear.recipient_out", senderName)
                    logger.info("Cleared inventory of ${it.name.string} with $counter items by $senderName")
                }
            } else {
                logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            }
            return 0
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(c.playerName(), "ess.clear.other")) {
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

                    var counter = 0
                    val players: MutableList<ServerPlayerEntity> = mutableListOf()

                    if (argument is String && argument == "targets") {
                        EntityArgument.getPlayers(c, "targets").forEach {
                            players.add(it)
                        }
                    } else {
                        players.add(targetPlayer)
                    }

                    players.forEach {
                        counter += it.inventory.clearMatchingItems({ true }, -1)
                        it.openContainer.detectAndSendChanges()
                        it.updateHeldItem()
                        sendMsg(c.source, "clear.other.success", targetName)
                        sendMsg(it.commandSource, "clear.recipient_out", senderName)
                        logger.info("Cleared inventory of ${it.name.string} with $counter items by $senderName")
                    }
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "clear.other.restricted")
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(c.playerName(), "ess.clear")) {
                    val itemCounts = senderPlayer.inventory.clearMatchingItems({ true }, -1)
                    senderPlayer.openContainer.detectAndSendChanges()
                    senderPlayer.updateHeldItem()
                    sendMsg(c.source, "clear.self.success")
                    logger.info("Cleared inventory of ${senderPlayer.name.string} with $itemCounts items")
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "clear.self.restricted")
                    return 0
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
