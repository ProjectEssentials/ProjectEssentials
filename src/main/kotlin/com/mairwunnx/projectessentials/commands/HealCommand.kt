package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

object HealCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.heal

    init {
        command = "heal"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.heal
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        "player", EntityArgument.player()
                    ).executes {
                        return@executes execute(
                            it,
                            true
                        )
                    }
                )
                .executes {
                    return@executes execute(
                        it
                    )
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
            if (targetIsExists) {
                if (!targetPlayer.shouldHeal()) {
                    logger.info("Player $targetName already fully healed.")
                    return 0
                }
                logger.info(
                    "Player ($targetName) Health changed from ${targetPlayer.health} to ${targetPlayer.maxHealth} by $senderName"
                )
                targetPlayer.health = targetPlayer.maxHealth
                logger.info("You have healed player $targetName.")
                sendMsg(
                    targetPlayer.commandSource,
                    "heal.other.recipient_out",
                    targetName
                )
            } else {
                throwOnlyPlayerCan(command)
            }
            return 0
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(senderName, "ess.heal.other")) {
                    when {
                        !config.enableArgs -> {
                            logger.warn(
                                DISABLED_COMMAND_ARG
                                    .replace("%0", senderName)
                                    .replace("%1", command)
                            )
                            sendMsg(sender, "common.arg.disabled", command)
                            return 0
                        }
                    }

                    if (!targetPlayer.shouldHeal()) {
                        sendMsg(sender, "heal.other.maxhealth", targetName)
                        return 0
                    }
                    logger.info(
                        "Player ($targetName) Health changed from ${targetPlayer.health} to ${targetPlayer.maxHealth} by $senderName"
                    )
                    targetPlayer.health = targetPlayer.maxHealth
                    sendMsg(sender, "heal.other.success", targetName)
                    sendMsg(
                        targetPlayer.commandSource,
                        "heal.other.recipient_out",
                        targetName
                    )
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "heal.other.restricted", targetName)
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.heal")) {
                    if (!senderPlayer.shouldHeal()) {
                        sendMsg(sender, "heal.self.maxhealth")
                        return 0
                    }
                    logger.info(
                        "Player ($senderName) changed from ${senderPlayer.health} to ${senderPlayer.maxHealth}"
                    )
                    senderPlayer.health = senderPlayer.maxHealth
                    sendMsg(sender, "heal.self.success")
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "heal.self.restricted", senderName)
                    return 0
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
