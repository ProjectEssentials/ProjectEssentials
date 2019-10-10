package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

open class CommandTimeBase : CommandBase() {
    private val logger = LogManager.getLogger()
    var time = 0L

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
            if (hasPermission()) {
                sender.world.dayTime = time
                sendMsg(sender, "$command.installed")
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "time.restricted", senderName)
                return 0
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }

    fun hasPermission(): Boolean {
        return (PermissionsAPI.hasPermission(senderName, "ess.$command") ||
                PermissionsAPI.hasPermission(senderName, "ess.time"))
    }
}
