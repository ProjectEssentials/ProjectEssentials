package com.mairwunnx.projectessentials.commands.health

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

@UnstableDefault
object AirCommand : CommandBase<CommandsConfig.Commands.Air>(
    getCommandsConfig().commands.air
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.air
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        commandArgName, EntityArgument.player()
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

        if (hasTarget) {
            if (targetPlayer.air == targetPlayer.maxAir) {
                sendMsg(sender, "air.player.maxair", targetPlayerName)
                return false
            }
            logger.info(
                "Player ($targetPlayerName) air level changed from ${targetPlayer.air} to ${targetPlayer.maxAir} by $senderNickName"
            )
            targetPlayer.air = targetPlayer.maxAir
            sendMsg(sender, "air.player.success", targetPlayerName)
            sendMsg(
                targetPlayer.commandSource,
                "air.player.recipient.success",
                senderNickName
            )
        } else {
            if (senderPlayer.air == senderPlayer.maxAir) {
                sendMsg(sender, "air.self.maxair")
                return false
            }
            logger.info(
                "Player ($senderNickName) air level changed from ${senderPlayer.air} to ${senderPlayer.maxAir}"
            )
            senderPlayer.air = senderPlayer.maxAir
            sendMsg(sender, "air.self.success")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
