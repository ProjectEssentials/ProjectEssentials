package com.mairwunnx.projectessentials.commands.weather

import com.mairwunnx.projectessentials.commands.CommandBase
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
object SunCommand : CommandBase<CommandsConfig.Commands.Sun>(
    getCommandsConfig().commands.sun,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.sun
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

        sender.world.worldInfo.clearWeatherTime = 0
        sender.world.worldInfo.thunderTime = 0
        sender.world.worldInfo.rainTime = 0
        sender.world.worldInfo.isRaining = false
        sender.world.worldInfo.isThundering = false
        sendMsg(sender, "sun.installed")

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
