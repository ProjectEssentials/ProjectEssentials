package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

@UnstableDefault
object ListCommand : CommandBase<CommandsConfig.Commands.List>(
    getCommandsConfig().commands.list,
    hasArguments = false,
    canServerExecuteWhenArgNone = true
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.list
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes {
                    execute(it)
                    return@executes 0
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean
    ): Boolean {
        val code = super.execute(c, hasTarget)
        if (!code) return false

        val maxListNodes = getCommandsConfig().commands.list.maxDisplayedPlayers
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

        if (senderNickName == "server") {
            logger.info("Players online ($online/$maxOnline): ${onlinePlayers()}")
        } else {
            sendMsg(
                sender, "list.out",
                online.toString(), maxOnline.toString(),
                onlinePlayers().toString()
            )
        }
        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
