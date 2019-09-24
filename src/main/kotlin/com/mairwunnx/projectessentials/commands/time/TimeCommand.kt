package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.get24TimeFromTicks
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

@UnstableDefault
object TimeCommand : CommandBase<CommandsConfig.Commands.Time>(
    getCommandsConfig().commands.time,
    hasArguments = false,
    serverCanExecute = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.time
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
        val commandName = c.input.drop(1)

        if (commandName == "time") {
            sendMsg(
                sender,
                "time.out",
                get24TimeFromTicks(sender.world.worldInfo.dayTime)
            )
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
