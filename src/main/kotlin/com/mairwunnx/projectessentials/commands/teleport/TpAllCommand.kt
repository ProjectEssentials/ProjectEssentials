package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

object TpAllCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpAll

    init {
        command = "tpall"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpAll
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument("player", EntityArgument.player()).executes {
                        return@executes execute(it, EntityArgument.getPlayer(it, "player"))
                    }
                )
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)

        if (PermissionsAPI.hasPermission(senderName, "ess.tpall", senderIsServer)) {
            val entity = argument as ServerPlayerEntity
            val entityPosX = entity.positionVec.x
            val entityPosY = entity.positionVec.y
            val entityPosZ = entity.positionVec.z
            val entityWorld = entity.serverWorld
            val entityYaw = entity.rotationYaw
            val entityPitch = entity.rotationPitch

            senderPlayer.server.playerList.players.forEach {
                it.teleport(entityWorld, entityPosX, entityPosY, entityPosZ, entityYaw, entityPitch)
            }
            sendMsg(sender, "tpall.success")
        } else {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", senderName)
                    .replace("%1", command)
            )
            sendMsg(sender, "tpall.restricted", senderName)
            return 0
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
