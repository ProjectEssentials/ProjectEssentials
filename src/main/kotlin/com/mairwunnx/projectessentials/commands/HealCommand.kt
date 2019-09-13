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
import org.apache.logging.log4j.Logger

/**
 * **Description:** Heals you or the given player.
 *
 * **Usage example:** `/heal` and `/eheal`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 *
 * **Can server use it command?:** `false`
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object HealCommand {
    private val logger: Logger = LogManager.getLogger()
    private const val HEAL_COMMAND: String = "heal"
    private const val HEAL_ARG_NAME_COMMAND: String = "player"
    private val healCommandAliases: MutableList<String> = mutableListOf(HEAL_COMMAND)

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("Starting register \"/$HEAL_COMMAND\" command ...")
        logger.info("Processing commands aliases for \"/$HEAL_COMMAND\" command ...")

        CommandAliases.aliases[HEAL_COMMAND] = modConfig.commands.heal.aliases.toMutableList()
        healCommandAliases.addAll(
            modConfig.commands.heal.aliases
        )

        healCommandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        HEAL_ARG_NAME_COMMAND,
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
        val permissionLevel = modConfig.commands.heal.permissionLevel
        val argUsePermissionLevel = modConfig.commands.heal.argUsePermissionLevel
        val isEnabledArgs = modConfig.commands.heal.enableArgs
        val isPlayerSender = c.isPlayerSender()
        lateinit var targetAsPlayer: ServerPlayerEntity
        var playerNickNameAsTarget: String = String.empty

        if (!isPlayerSender) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", HEAL_COMMAND))
            return
        }

        if (hasTarget) {
            targetAsPlayer = EntityArgument.getPlayer(
                c, HEAL_ARG_NAME_COMMAND
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
                    .replace("%1", HEAL_COMMAND)
            )
            sendMsg(commandSender, "common.arg.error", HEAL_COMMAND)
            return
        }

        if (!commandSenderPlayer.hasPermissionLevel(permissionLevel)) {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", HEAL_COMMAND)
            )

            if (hasTarget) {
                sendMsg(commandSender, "heal.player.error", playerNickNameAsTarget)
            } else {
                sendMsg(commandSender, "heal.self.error")
            }
            return
        }

        if (hasTarget) {
            if (!commandSenderPlayer.hasPermissionLevel(argUsePermissionLevel)) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", HEAL_COMMAND)
                )
                sendMsg(commandSender, "heal.player.error", playerNickNameAsTarget)
                return
            }

            if (!targetAsPlayer.shouldHeal()) {
                sendMsg(commandSender, "heal.player.maxhealth", playerNickNameAsTarget)
                return
            }
            logger.info(
                "Player ($playerNickNameAsTarget) Health changed from ${commandSenderPlayer.health} to ${commandSenderPlayer.maxHealth} by $commandSenderNickName"
            )
            targetAsPlayer.health = targetAsPlayer.maxHealth
            sendMsg(commandSender, "heal.player.success", playerNickNameAsTarget)
            sendMsg(
                targetAsPlayer.commandSource,
                "heal.player.recipient.success",
                commandSenderNickName
            )
        } else {
            if (!commandSenderPlayer.shouldHeal()) {
                sendMsg(commandSender, "heal.self.maxhealth")
                return
            }
            logger.info(
                "Player ($commandSenderNickName) Health changed from ${commandSenderPlayer.health} to ${commandSenderPlayer.maxHealth}"
            )
            commandSenderPlayer.health = commandSenderPlayer.maxHealth
            sendMsg(commandSender, "heal.self.success")
        }

        logger.info("Executed command \"/$HEAL_COMMAND\" from $commandSenderNickName")
    }
}
