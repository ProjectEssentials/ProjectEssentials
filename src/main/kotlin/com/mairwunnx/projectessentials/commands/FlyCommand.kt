package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.commands.helpers.CommandAliases
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.dimName
import com.mairwunnx.projectessentials.extensions.empty
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
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
@UnstableDefault
object FlyCommand {
    private val logger = LogManager.getLogger()
    private const val FLY_COMMAND = "fly"
    private const val FLY_ARG_NAME_COMMAND = "player"
    private val flyCommandAliases = mutableListOf(FLY_COMMAND)

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("    - register \"/$FLY_COMMAND\" command ...")

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
        isAutoFly: Boolean = false,
        isHasTarget: Boolean = false
    ): Boolean {
        val source = target.commandSource
        val abilities = target.abilities

        if (source.asPlayer().isCreative || source.asPlayer().isSpectator) {
            if (!isAutoFly && !isHasTarget) {
                sendMsg(source, "fly.creative")
            }
            return false
        }

        val store = StorageBase.getData(target.uniqueID.toString())

        abilities.allowEdit = true
        fun installFly(install: Boolean) {
            if (!target.isCreative && !target.isSpectator) {
                abilities.allowFlying = install
                abilities.isFlying = install
                source.asPlayer().sendPlayerAbilities()
            }
        }

        if (isAutoFly) {
            return if (store.flyEnabledInWorlds.contains(target.world.worldInfo.worldName)) {
                if (isRestrictedWorld(target)) {
                    installFly(false)
                    false
                } else {
                    installFly(true)
                    true
                }
            } else {
                installFly(false)
                false
            }
        }

        if (isRestrictedWorld(target)) {
            installFly(false)
            if (!isHasTarget) {
                sendMsg(target.commandSource, "fly.self.restricted")
            }
            return false
        }

        abilities.allowEdit = true
        if (source.asPlayer().onGround) {
            abilities.allowFlying = !abilities.allowFlying
            abilities.isFlying = !abilities.allowFlying
        } else {
            abilities.allowFlying = !abilities.isFlying
            abilities.isFlying = !abilities.isFlying
        }
        source.asPlayer().sendPlayerAbilities()
        return true
    }

    private fun isRestrictedWorld(
        target: ServerPlayerEntity
    ): Boolean {
        val flyConfig = ModConfiguration.getCommandsConfig().commands.fly
        return if (flyConfig.flyDisabledWorlds.contains(target.world.dimName())) {
            !target.hasPermissionLevel(flyConfig.disabledWorldsBypassPermLevel)
        } else {
            false
        }
    }
}
