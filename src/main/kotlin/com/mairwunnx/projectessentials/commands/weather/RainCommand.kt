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
object RainCommand : CommandBase<CommandsConfig.Commands.Rain>(
    getCommandsConfig().commands.rain,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.rain
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
            logger.info("1")
            val duration = IntegerArgumentType.getInteger(c, "duration")
            logger.info("2")
            when {
                duration <= 0 -> sender.world.worldInfo.rainTime =
                    config.commands.rain.defaultDuration
                else -> sender.world.worldInfo.rainTime = duration
            }
            sender.world.worldInfo.thunderTime = 0
            sender.world.worldInfo.isRaining = true
            sender.world.worldInfo.isThundering = false
        } else {
            sender.world.worldInfo.rainTime = config.commands.rain.defaultDuration
            sender.world.worldInfo.thunderTime = 0
            sender.world.worldInfo.isRaining = true
            sender.world.worldInfo.isThundering = false
        }
        sendMsg(sender, "rain.installed")

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
