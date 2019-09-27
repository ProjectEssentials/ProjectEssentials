package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.world.gen.Heightmap.Type
import org.apache.logging.log4j.LogManager

@UnstableDefault
object TopCommand : CommandBase<CommandsConfig.Commands.Top>(
    getCommandsConfig().commands.top,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()
    private const val topYPosModifier = 1.4
    private const val centerOfBlockPos = 0.5

    override fun reload() {
        commandInstance = getCommandsConfig().commands.top
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
        if (hasTarget) return false

        val position = senderPlayer.position
        val heightTop = senderPlayer.world
            .getChunkAt(position)
            .getTopBlockY(
                Type.MOTION_BLOCKING,
                position.x,
                position.z
            ) + topYPosModifier

        logger.info(
            "Player ($senderNickName) top pos (y) changed from ${position.y.toDouble()} to $heightTop"
        )
        senderPlayer.setPositionAndUpdate(
            position.x + centerOfBlockPos,
            heightTop,
            position.z + centerOfBlockPos
        )
        sendMsg(sender, "top.success")

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
