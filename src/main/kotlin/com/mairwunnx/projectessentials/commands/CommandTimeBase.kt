package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

abstract class CommandTimeBase : CommandBase() {
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
            throwOnlyPlayerCan(command)
            return 0
        } else {
            if (hasPermission()) {
                sender.world.dayTime = time
                sendMsg(sender, "$command.installed")
            } else {
                throwPermissionLevel(senderName, command)
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
