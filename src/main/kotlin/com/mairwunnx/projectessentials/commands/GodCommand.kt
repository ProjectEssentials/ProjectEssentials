package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.dimName
import com.mairwunnx.projectessentials.extensions.sendMsg
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

@UnstableDefault
object GodCommand : CommandBase<CommandsConfig.Commands.God>(
    getCommandsConfig().commands.god
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.god
        super.reload()
    }

    override fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        commandArgName,
                        EntityArgument.player()
                    ).executes {
                        execute(it, true)
                        return@executes 0
                    }
                )
                .executes {
                    execute(it)
                    return@executes 0
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean
    ): Boolean {
        val code = super.execute(c, hasTarget)
        if (!code) return false

        val playerAbilities = targetPlayer.abilities
        if (hasTarget) {
            logger.info(
                "Player ($targetPlayerName) god state changed from ${playerAbilities.disableDamage} to ${!playerAbilities.disableDamage} by $senderNickName"
            )
            if (!setGod(targetPlayer, isHasTarget = true)) return false
            if (senderNickName == "server") {
                logger.info("You changed god mode of player $targetPlayerName.")
            } else {
                sendMsg(sender, "god.player.success", targetPlayerName)
            }
            sendMsg(
                targetPlayer.commandSource,
                "god.player.recipient.success",
                senderNickName
            )
        } else {
            logger.info(
                "Player ($senderNickName) god state changed from ${playerAbilities.disableDamage} to ${!playerAbilities.disableDamage}"
            )
            if (!setGod(sender.asPlayer())) return false
            sendMsg(sender, "god.self.success")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
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
                sendMsg(sender, "god.creative")
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
            if (!isHasTarget) sendMsg(sender, "god.self.restricted")
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
            !target.hasPermissionLevel(godConfig.disabledWorldsBypassPermLevel)
        } else {
            false
        }
    }
}
