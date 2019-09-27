package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.commands.helpers.CommandAliases
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
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
 *
 * **Can server use it command?:** `false`.
 */
@UnstableDefault
object TopCommand {
    private val logger: Logger = LogManager.getLogger()
    private const val TOP_COMMAND: String = "top"
    private const val topYPosModifier: Double = 1.4
    private const val centerOfBlockPos: Double = 0.5
    private val topCommandAliases: MutableList<String> = mutableListOf(TOP_COMMAND)

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("    - register \"/$TOP_COMMAND\" command ...")

        CommandAliases.aliases[TOP_COMMAND] = modConfig.commands.top.aliases.toMutableList()
        topCommandAliases.addAll(modConfig.commands.top.aliases)

        topCommandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes {
                    execute(it)
                    return@executes 1
                }
            )
        }
    }

    private fun execute(c: CommandContext<CommandSource>) {
        val modConfig = ModConfiguration.getCommandsConfig()
        val permissionLevel = modConfig.commands.top.permissionLevel
        val isPlayerSender = c.isPlayerSender()

        if (!isPlayerSender) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", TOP_COMMAND))
            return
        }

        val commandSender = c.source
        val commandSenderPlayer = commandSender.asPlayer()
        val commandSenderNickName = commandSenderPlayer.name.string

        if (!commandSenderPlayer.hasPermissionLevel(permissionLevel)) {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", TOP_COMMAND)
            )
            sendMsg(commandSender, "top.error")
            return
        }

        val position = commandSenderPlayer.position
        val heightTop = commandSenderPlayer.world
            .getChunkAt(position)
            .getTopBlockY(
                Heightmap.Type.MOTION_BLOCKING,
                position.x,
                position.z
            ) + topYPosModifier

        logger.info(
            "Player ($commandSenderNickName) top pos (y) changed from ${position.y.toDouble()} to $heightTop"
        )
        commandSenderPlayer.setPositionAndUpdate(
            position.x + centerOfBlockPos,
            heightTop,
            position.z + centerOfBlockPos
        )
        sendMsg(commandSender, "top.success")
        logger.info("Executed command \"/$TOP_COMMAND\" from $commandSenderNickName")
    }
}
