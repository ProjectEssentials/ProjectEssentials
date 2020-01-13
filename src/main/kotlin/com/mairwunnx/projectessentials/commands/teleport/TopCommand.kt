package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.world.gen.Heightmap.Type
import org.apache.logging.log4j.LogManager

object TopCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private const val topYPosModifier = 1.4
    private const val centerOfBlockPos = 0.5
    private var config = getCommandsConfig().commands.top

    init {
        command = "top"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.top
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes {
                    return@executes execute(it)
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)
        if (senderIsServer) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.top")) {
                val position = senderPlayer.position
                val heightTop = senderPlayer.world
                    .getChunkAt(position)
                    .getTopBlockY(
                        Type.MOTION_BLOCKING,
                        position.x,
                        position.z
                    ) + topYPosModifier

                logger.info(
                    "Player ($senderName) top pos (y) changed from ${position.y.toDouble()} to $heightTop"
                )
                senderPlayer.setPositionAndUpdate(
                    position.x + centerOfBlockPos,
                    heightTop,
                    position.z + centerOfBlockPos
                )
                sendMsg(sender, "top.success")
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "top.restricted", senderName)
                return 0
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
