package com.mairwunnx.projectessentials.commands.abilities

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.dimName
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

object FlyCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.fly

    init {
        command = "fly"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.fly
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
                        return@executes execute(it, true)
                    }
                )
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
            if (targetIsExists) {
                val playerAbilities = targetPlayer.abilities
                logger.info(
                    "Player ($targetName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying} by $senderName"
                )
                if (!setFly(targetPlayer)) return 0
                logger.info("You changed fly mode of player $targetName.")
                sendMsg(
                    targetPlayer.commandSource,
                    "fly.other.recipient_out",
                    senderName
                )
            } else {
                throwOnlyPlayerCan(command)
            }
            return 0
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(senderName, "ess.fly.other")) {
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

                    val playerAbilities = targetPlayer.abilities
                    logger.info(
                        "Player ($targetName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying} by $senderName"
                    )
                    if (!setFly(targetPlayer)) return 0
                    sendMsg(sender, "fly.other.success", targetName)
                    sendMsg(
                        target,
                        "fly.other.recipient_out",
                        senderName
                    )
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "fly.other.restricted", targetName)
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.fly")) {
                    val playerAbilities = senderPlayer.abilities
                    logger.info(
                        "Player ($senderName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying}"
                    )
                    if (!setFly(senderPlayer)) return 0
                    sendMsg(sender, "fly.self.success")
                } else {
                    throwPermissionLevel(senderName, command)
                    sendMsg(sender, "fly.self.restricted")
                    return 0
                }
            }
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
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
                sendMsg(source, "fly.incompatible_mode")
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
                sendMsg(target.commandSource, "fly.incompatible_world")
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
        val flyConfig = getCommandsConfig().commands.fly
        return if (flyConfig.flyDisabledWorlds.contains(target.world.dimName())) {
            !PermissionsAPI.hasPermission(
                target.name.string,
                "fly.incompatible_world.bypass"
            )
        } else {
            false
        }
    }
}
