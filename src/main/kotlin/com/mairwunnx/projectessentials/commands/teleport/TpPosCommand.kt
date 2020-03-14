package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import org.apache.logging.log4j.LogManager

object TpPosCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = ModConfiguration.getCommandsConfig().commands.tpPos

    init {
        command = "tppos"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = ModConfiguration.getCommandsConfig().commands.tpPos
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument("position", BlockPosArgument.blockPos()).executes {
                        return@executes execute(it)
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

        if (senderIsServer) {
            throwOnlyPlayerCan(command)
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.tppos")) {
                val position = BlockPosArgument.getBlockPos(c, "position")
                senderPlayer.teleport(
                    senderPlayer.serverWorld,
                    position.x.toDouble() + 0.5,
                    position.y.toDouble(),
                    position.z.toDouble() + 0.5,
                    senderPlayer.rotationYaw,
                    senderPlayer.rotationPitch
                )
                sendMsg(sender, "tppos.success")
            } else {
                throwPermissionLevel(senderName, command)
                sendMsg(sender, "tppos.restricted", senderName)
                return 0
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
