package com.mairwunnx.projectessentials.commands

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
import kotlin.math.roundToInt

@UnstableDefault
object GetPosCommand : CommandBase<CommandsConfig.Commands.GetPos>(
    getCommandsConfig().commands.getPos
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.getPos
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        commandArgName,
                        EntityArgument.player()
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
            val posX = targetPlayer.posX.roundToInt()
            val posY = targetPlayer.posY.roundToInt()
            val posZ = targetPlayer.posZ.roundToInt()
            if (senderNickName == "server") {
                logger.info("Player $targetPlayerName current position XYZ: $posX / $posY / $posZ")
            } else {
                sendMsg(
                    sender,
                    "getpos.player.out",
                    targetPlayerName,
                    posX.toString(),
                    posY.toString(),
                    posZ.toString()
                )
            }
        } else {
            sendMsg(
                sender,
                "getpos.self.out",
                senderPlayer.posX.roundToInt().toString(),
                senderPlayer.posY.roundToInt().toString(),
                senderPlayer.posZ.roundToInt().toString()
            )
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
