package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
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
        private val topCommandAliases: MutableList<String> = mutableListOf(TOP_COMMAND)
        private const val topYPosModifier: Double = 1.4
        private const val centerOfBlockPos: Double = 0.5

        fun register(
            dispatcher: CommandDispatcher<CommandSource>
        ) {
            logger.info("Starting register \"/$TOP_COMMAND\" command ...")
            logger.info("Processing commands aliases for \"/$TOP_COMMAND\" command ...")

            topCommandAliases.addAll(
                ModConfiguration.getCommandsConfig().commands.top.commandAliases
            )

            registerAliases()

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
            val modConfig = ModConfiguration.getCommandsConfig()
            if (!c.isPlayerSender()) {
                logger.warn(
                    "\"/$TOP_COMMAND\" command should only be used by the player!"
                )
                return
            }

            val commandSenderNickName: String = c.source.asPlayer().name.string
            val commandSender: CommandSource = c.source

            if (!commandSender.asPlayer().hasPermissionLevel(
                    modConfig.commands.top.permissionLevel
                )
            ) {
                logger.info(
                    "Player ($commandSenderNickName) failed to executing \"/$TOP_COMMAND\" command"
                )
                commandSender.sendFeedback(
                    TranslationTextComponent(
                        "project_essentials.top.error"
                    ),
                    true
                )
                return
            }

            logger.info("Executed command \"/$TOP_COMMAND\" from $commandSenderNickName")

            val position = commandSender.asPlayer().position
            val heightTop = commandSender.asPlayer().world
                .getChunkAt(position)
                .getTopBlockY(
                    Heightmap.Type.MOTION_BLOCKING,
                    position.x,
                    position.z
                ) + topYPosModifier

            logger.info(
                "Player ($commandSenderNickName) top pos (y) changed from ${position.y.toDouble()} to $heightTop"
            )
            commandSender.asPlayer().setPositionAndUpdate(
                position.x + centerOfBlockPos,
                heightTop,
                position.z + centerOfBlockPos
            )
            commandSender.sendFeedback(
                TranslationTextComponent(
                    "project_essentials.top.success"
                ),
                true
            )
        }

        private fun registerAliases() {
            CommandAliases.aliases[TOP_COMMAND] = topCommandAliases
        }
    }
}
