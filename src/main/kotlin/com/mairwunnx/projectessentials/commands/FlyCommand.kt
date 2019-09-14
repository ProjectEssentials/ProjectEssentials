package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
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
 * **Description:** Add ability fly for player or target player.
 *
 * **Usage example:** `/fly` and `/efly`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 *
 * **Can server use it command?:** `false`.
 */
object FlyCommand {
    private val logger = LogManager.getLogger()
    private const val FLY_COMMAND = "fly"
    private const val FLY_ARG_NAME_COMMAND = "player"
    private val flyCommandAliases = mutableListOf(FLY_COMMAND)

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("Starting register \"/$FLY_COMMAND\" command ...")

        CommandAliases.aliases[FLY_COMMAND] = modConfig.commands.fly.aliases.toMutableList()
        flyCommandAliases.addAll(modConfig.commands.fly.aliases)

        flyCommandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        FLY_ARG_NAME_COMMAND,
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
        val permissionLevel = modConfig.commands.fly.permissionLevel
        val argUsePermissionLevel = modConfig.commands.fly.argUsePermissionLevel
        val isEnabledArgs = modConfig.commands.fly.enableArgs
        val isPlayerSender = c.isPlayerSender()
        lateinit var targetAsPlayer: ServerPlayerEntity
        var playerNickNameAsTarget = String.empty

        if (!isPlayerSender) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", FLY_COMMAND))
            return
        }

        if (hasTarget) {
            targetAsPlayer = EntityArgument.getPlayer(
                c, FLY_ARG_NAME_COMMAND
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
                    .replace("%1", FLY_COMMAND)
            )
            sendMsg(commandSender, "common.arg.error", FLY_COMMAND)
            return
        }

        if (!commandSenderPlayer.hasPermissionLevel(permissionLevel)) {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", FLY_COMMAND)
            )
            if (hasTarget) {
                sendMsg(commandSender, "fly.player.error", playerNickNameAsTarget)
            } else {
                sendMsg(commandSender, "fly.self.error")
            }
            return
        }

        if (hasTarget) {
            val playerAbilities = targetAsPlayer.abilities
            if (!commandSenderPlayer.hasPermissionLevel(argUsePermissionLevel)) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", FLY_COMMAND)
                )
                sendMsg(commandSender, "fly.player.error", playerNickNameAsTarget)
                return
            }

            logger.info(
                "Player ($playerNickNameAsTarget) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying} by $commandSenderNickName"
            )
            if (!setFly(targetAsPlayer)) return
            sendMsg(commandSender, "fly.player.success", playerNickNameAsTarget)
            sendMsg(
                targetAsPlayer.commandSource,
                "fly.player.recipient.success",
                commandSenderNickName
            )
        } else {
            val playerAbilities = commandSenderPlayer.abilities
            logger.info(
                "Player ($commandSenderNickName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying}"
            )
            if (!setFly(commandSenderPlayer)) return
            sendMsg(commandSender, "fly.self.success")
        }

        logger.info("Executed command \"/$FLY_COMMAND\" from $commandSenderNickName")
    }

    fun setFly(
        target: ServerPlayerEntity,
        isAutoFly: Boolean = false
    ): Boolean {
        val source = target.commandSource
        val abilities = target.abilities
        val targetNickName = source.asPlayer().name.string
        val config = ModConfiguration.getCommandsConfig()
        if (source.asPlayer().isCreative || source.asPlayer().isSpectator) {
            if (!isAutoFly) {
                sendMsg(source, "fly.creative")
            }
            return false
        }

        if (isAutoFly) {
            if (config.commands.fly.autoFly.contains(targetNickName)) {
                abilities.allowEdit = true
                abilities.allowFlying = true
                abilities.isFlying = true
                source.asPlayer().sendPlayerAbilities()
                return true
            }
        }

        abilities.allowEdit = true
        if (source.asPlayer().onGround) {
            abilities.allowFlying = !abilities.allowFlying
            abilities.isFlying = !abilities.allowFlying
        } else {
            abilities.allowFlying = !abilities.isFlying
            abilities.isFlying = !abilities.isFlying
        }

        if (config.commands.fly.autoFlyEnabled && !isAutoFly) {
            if (source.asPlayer().onGround) {
                if (abilities.allowFlying) {
                    saveToAutoFly(config, targetNickName)
                } else {
                    removeFromAutoFly(config, targetNickName)
                }
            } else {
                if (abilities.isFlying) {
                    saveToAutoFly(config, targetNickName)
                } else {
                    removeFromAutoFly(config, targetNickName)
                }
            }
        }
        source.asPlayer().sendPlayerAbilities()
        return true
    }

    private fun saveToAutoFly(
        config: CommandsConfig,
        targetNickName: String
    ) {
        if (!config.commands.fly.autoFly.contains(targetNickName)) {
            val list = config.commands.fly.autoFly.toMutableList()
            list.add(targetNickName)
            config.commands.fly.autoFly = list.toList()
        }
    }

    private fun removeFromAutoFly(
        config: CommandsConfig,
        targetNickName: String
    ) {
        if (config.commands.fly.autoFly.contains(targetNickName)) {
            val list = config.commands.fly.autoFly.toMutableList()
            list.remove(targetNickName)
            config.commands.fly.autoFly = list.toList()
        }
    }
}
