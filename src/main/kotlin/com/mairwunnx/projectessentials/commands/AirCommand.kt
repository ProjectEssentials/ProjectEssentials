package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.empty
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

/**
 * **Description:** Add player or target player air.
 *
 * **Usage example:** `/air` and `/eair`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 *
 * **Can server use it command?:** `false`.
 */
object AirCommand {
    private val logger = LogManager.getLogger()
    private const val AIR_COMMAND = "air"
    private const val AIR_ARG_NAME_COMMAND = "player"
    private val airCommandAliases = mutableListOf(AIR_COMMAND)

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("Starting register \"/$AIR_COMMAND\" command ...")

        CommandAliases.aliases[AIR_COMMAND] = modConfig.commands.air.aliases.toMutableList()
        airCommandAliases.addAll(modConfig.commands.air.aliases)

        airCommandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        AIR_ARG_NAME_COMMAND,
                        EntityArgument.player()
                    ).executes {
                        execute(it, true)
                        return@executes 1
                    }
                )
                .executes {
                    execute(it)
                    return@executes 1
                }
            )
        }
    }

    private fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean = false
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        val permissionLevel = modConfig.commands.air.permissionLevel
        val argUsePermissionLevel = modConfig.commands.air.argUsePermissionLevel
        val isEnabledArgs = modConfig.commands.air.enableArgs
        val isPlayerSender = c.isPlayerSender()
        lateinit var targetAsPlayer: ServerPlayerEntity
        var playerNickNameAsTarget = String.empty

        if (!isPlayerSender) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", AIR_COMMAND))
            return
        }

        if (hasTarget) {
            targetAsPlayer = EntityArgument.getPlayer(
                c, AIR_ARG_NAME_COMMAND
            )
            playerNickNameAsTarget = targetAsPlayer.name.string
        }

        val commandSender = c.source
        val commandSenderPlayer = commandSender.asPlayer()
        val commandSenderNickName = commandSenderPlayer.name.string

        if (hasTarget && !isEnabledArgs) {
            logger.warn(
                DISABLED_COMMAND_ARG
                    .replace("%0", commandSenderNickName)
                    .replace("%1", AIR_COMMAND)
            )
            sendMsg(commandSender, "common.arg.error", AIR_COMMAND)
            return
        }

        if (!commandSenderPlayer.hasPermissionLevel(permissionLevel)) {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", AIR_COMMAND)
            )
            if (hasTarget) {
                sendMsg(commandSender, "air.player.error", playerNickNameAsTarget)
            } else {
                sendMsg(commandSender, "air.self.error")
            }
            return
        }

        if (hasTarget) {
            if (!commandSenderPlayer.hasPermissionLevel(argUsePermissionLevel)) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", AIR_COMMAND)
                )
                sendMsg(commandSender, "air.player.error", playerNickNameAsTarget)
                return
            }

            if (targetAsPlayer.air == targetAsPlayer.maxAir) {
                sendMsg(commandSender, "air.player.maxair", playerNickNameAsTarget)
                return
            }
            logger.info(
                "Player ($playerNickNameAsTarget) air level changed from ${targetAsPlayer.air} to ${targetAsPlayer.maxAir} by $commandSenderNickName"
            )
            targetAsPlayer.air = targetAsPlayer.maxAir
            sendMsg(commandSender, "air.player.success", playerNickNameAsTarget)
            sendMsg(
                targetAsPlayer.commandSource,
                "air.player.recipient.success",
                commandSenderNickName
            )
        } else {
            if (commandSenderPlayer.air == commandSenderPlayer.maxAir) {
                sendMsg(commandSender, "air.self.maxair")
                return
            }
            logger.info(
                "Player ($commandSenderNickName) air level changed from ${commandSenderPlayer.air} to ${commandSenderPlayer.maxAir}"
            )
            commandSenderPlayer.air = commandSenderPlayer.maxAir
            sendMsg(commandSender, "air.self.success")
        }

        logger.info("Executed command \"/$AIR_COMMAND\" from $commandSenderNickName")
    }
}
