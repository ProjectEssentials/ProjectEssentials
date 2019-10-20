package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

// todo: add registerNative method for overriding ListCommand from minecraft.

object ListCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.list

    init {
        command = "list"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.list
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
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
        val maxListNodes = config.maxDisplayedPlayers
        val online = sender.server.onlinePlayerNames.count()
        val maxOnline = sender.server.maxPlayers
        val onlinePlayers = fun(): List<String> {
            return if (online > maxListNodes) {
                sender.server.onlinePlayerNames.slice(
                    IntRange(0, maxListNodes)
                )
            } else {
                sender.server.onlinePlayerNames.toList()
            }
        }

        if (senderIsServer) {
            logger.info("Players online ($online/$maxOnline): ${onlinePlayers()}")
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.list")) {
                sendMsg(
                    sender, "list.out",
                    online.toString(), maxOnline.toString(), onlinePlayers().toString()
                )
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "list.restricted", senderName)
                return 0
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
