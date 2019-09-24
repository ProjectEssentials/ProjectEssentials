package com.mairwunnx.projectessentials.commands.weather

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

@UnstableDefault
object StormCommand : CommandBase<CommandsConfig.Commands.Storm>(
    getCommandsConfig().commands.storm,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.storm
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        "duration",
                        IntegerArgumentType.integer()
                    ).executes {
                        execute(it, true)
                        return@executes 0
                    }
                )
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

        sender.world.worldInfo.clearWeatherTime = 0
        if (hasTarget) {
            val duration = IntegerArgumentType.getInteger(c, "duration")
            when {
                duration <= 0 -> sender.world.worldInfo.thunderTime =
                    config.commands.storm.defaultDuration
                else -> sender.world.worldInfo.thunderTime = duration
            }
            sender.world.worldInfo.rainTime = 0
            sender.world.worldInfo.isRaining = false
            sender.world.worldInfo.isThundering = true
        } else {
            sender.world.worldInfo.thunderTime = config.commands.storm.defaultDuration
            sender.world.worldInfo.rainTime = 0
            sender.world.worldInfo.isRaining = false
            sender.world.worldInfo.isThundering = true
        }
        sendMsg(sender, "storm.installed")

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
