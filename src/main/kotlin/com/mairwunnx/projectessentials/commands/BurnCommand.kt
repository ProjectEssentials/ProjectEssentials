package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

object BurnCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.burn

    init {
        command = "burn"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.burn
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument("player", EntityArgument.player()).then(
                        Commands.argument(
                            "duration",
                            IntegerArgumentType.integer(1, 120)
                        ).executes {
                            return@executes execute(
                                it, IntegerArgumentType.getInteger(it, "duration")
                            )
                        }
                    ).executes {
                        return@executes execute(it, config.defaultDuration)
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

        if (PermissionsAPI.hasPermission(senderName, "ess.burn", senderIsServer)) {
            val target = EntityArgument.getPlayer(c, "player")
            target.setFire(argument as Int)

            if (senderIsServer) {
                logger.info("You set ${target.name.string} on fire for $argument seconds.")
                return 0
            } else {
                sendMsg(sender, "burn.success", target.name.string, argument.toString())
            }
        } else {
            throwPermissionLevel(senderName, command)
            sendMsg(sender, "burn.restricted", senderName)
            return 0
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
