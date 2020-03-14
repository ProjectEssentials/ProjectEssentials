package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
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

object TpHereCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpHere

    init {
        command = "tphere"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpHere
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

        if (PermissionsAPI.hasPermission(senderName, "ess.tphere", senderIsServer)) {
            val senderPosX = senderPlayer.posX
            val senderPosY = senderPlayer.posY
            val senderPosZ = senderPlayer.posZ
            val senderWorld = senderPlayer.serverWorld
            val senderYaw = senderPlayer.rotationYaw
            val senderPitch = senderPlayer.rotationPitch

            val target = argument as ServerPlayerEntity
            target.teleport(senderWorld, senderPosX, senderPosY, senderPosZ, senderYaw, senderPitch)

            sendMsg(sender, "tphere.success", target.name.string)
        } else {
            throwPermissionLevel(senderName, command)
            sendMsg(sender, "tphere.restricted", senderName)
            return 0
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
