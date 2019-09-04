package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.world.gen.Heightmap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * **Description:** Teleport to the highest block at your current position.
 *
 * **Usage example:** `/top` and `/etop`.
 *
 * **Available arguments:** none.
 */
class TopCommand {
    companion object {
        private val logger: Logger = LogManager.getLogger()
        private const val TOP_COMMAND: String = "top"
        private val topCommandAliases: Array<String> = arrayOf(TOP_COMMAND, "etop")

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$TOP_COMMAND\" command ...")

            topCommandAliases.forEach { command ->
                dispatcher.register(
                    LiteralArgumentBuilder.literal<CommandSource>(command)
                        .executes {
                            execute(it)
                            return@executes 1
                        }
                )
            }
        }

        private fun execute(c: CommandContext<CommandSource>) {
            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!commandSender.asPlayer().hasPermissionLevel(2)) {
                logger.info(
                    "Player ($commandSenderNickName) failed to executing \"/$TOP_COMMAND\" command"
                )
                return
            }

            val position = commandSender.asPlayer().position
            logger.info("current player pos: X: ${position.x}, Y: ${position.y}, Z: ${position.z}")
            val heightTop = commandSender.asPlayer().world.getChunkAt(position)
                .getTopBlockY(
                    Heightmap.Type.MOTION_BLOCKING,
                    position.x,
                    position.z
                ) + 1.3
            commandSender.asPlayer().setPositionAndUpdate(
                position.x + 0.5,
                heightTop,
                position.z + 0.5
            )
        }
    }
}
