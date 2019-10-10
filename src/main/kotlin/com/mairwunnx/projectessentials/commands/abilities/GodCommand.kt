package com.mairwunnx.projectessentials.commands.abilities

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.dimName
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mairwunnx.projectessentialscore.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

object GodCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.god

    init {
        command = "god"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.god
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
                    "Player ($targetName) god state changed from ${playerAbilities.disableDamage} to ${!playerAbilities.disableDamage} by $senderName"
                )
                if (!setGod(targetPlayer, isHasTarget = true)) return 0
                logger.info("You changed god mode of player $targetName.")
                sendMsg(
                    target,
                    "god.other.recipient_out",
                    senderName
                )
            } else {
                logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            }
            return 0
        } else {
            if (targetIsExists) {
                val playerAbilities = targetPlayer.abilities
                if (PermissionsAPI.hasPermission(senderName, "ess.god.other")) {
                    when {
                        !config.enableArgs -> {
                            logger.warn(
                                DISABLED_COMMAND_ARG
                                    .replace("%0", senderName)
                                    .replace("%1", command)
                            )
                            sendMsg(sender, "common.arg.error", command)
                            return 0
                        }
                    }

                    logger.info(
                        "Player ($targetName) god state changed from ${playerAbilities.disableDamage} to ${!playerAbilities.disableDamage} by $senderName"
                    )
                    if (!setGod(targetPlayer, isHasTarget = true)) return 0
                    sendMsg(sender, "god.other.success", targetName)
                    sendMsg(
                        target,
                        "god.other.recipient_out",
                        senderName
                    )
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "god.other.restricted", targetName)
                    return 0
                }
            } else {
                val playerAbilities = senderPlayer.abilities
                if (PermissionsAPI.hasPermission(senderName, "ess.god")) {
                    logger.info(
                        "Player ($senderName) god state changed from ${playerAbilities.disableDamage} to ${!playerAbilities.disableDamage}"
                    )
                    if (!setGod(sender.asPlayer())) return 0
                    sendMsg(sender, "god.self.success")
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "god.self.restricted", senderName)
                    return 0
                }
            }
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }

    /**
     * Return false if need to cancel command executing;
     * return else if need to continue command executing.
     */
    fun setGod(
        target: ServerPlayerEntity,
        isAutoGod: Boolean = false,
        isHasTarget: Boolean = false
    ): Boolean {
        val targetAbilities = target.abilities

        if (target.isCreative || target.isSpectator) {
            if (!isAutoGod && !isHasTarget) {
                sendMsg(sender, "god.incompatible_mode")
            }
            return false
        }

        val store = StorageBase.getData(target.uniqueID.toString())
        targetAbilities.allowEdit = true

        fun installGod(install: Boolean) {
            if (!target.isCreative && !target.isSpectator) {
                targetAbilities.disableDamage = install
                target.sendPlayerAbilities()
            }
        }

        if (isAutoGod) {
            return if (store.godEnabledWorlds.contains(target.world.worldInfo.worldName)) {
                if (isRestrictedWorld(target)) {
                    installGod(false)
                    false
                } else {
                    installGod(true)
                    true
                }
            } else {
                installGod(false)
                false
            }
        }

        if (isRestrictedWorld(target)) {
            installGod(false)
            if (!isHasTarget) sendMsg(sender, "god.incompatible_world")
            return false
        }

        installGod(!targetAbilities.disableDamage)
        return true
    }

    private fun isRestrictedWorld(
        target: ServerPlayerEntity
    ): Boolean {
        val godConfig = getCommandsConfig().commands.god
        return if (godConfig.godModeDisabledWorlds.contains(target.world.dimName())) {
            !PermissionsAPI.hasPermission(target.name.string, "god.incompatible_world.bypass")
        } else {
            false
        }
    }
}
