package com.mairwunnx.projectessentials.commands.abilities

import com.mairwunnx.projectessentials.commands.CommandBase
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
object FlyCommand : CommandBase<CommandsConfig.Commands.Fly>(
    getCommandsConfig().commands.fly
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.fly
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        commandArgName, EntityArgument.player()
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

        if (hasTarget) {
            val playerAbilities = targetPlayer.abilities
            logger.info(
                "Player ($targetPlayerName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying} by $senderNickName"
            )
            if (!setFly(targetPlayer)) return false
            sendMsg(sender, "fly.player.success", targetPlayerName)
            sendMsg(
                targetPlayer.commandSource,
                "fly.player.recipient.success",
                senderNickName
            )
        } else {
            val playerAbilities = senderPlayer.abilities
            logger.info(
                "Player ($senderNickName) fly state changed from ${playerAbilities.isFlying} to ${!playerAbilities.isFlying}"
            )
            if (!setFly(senderPlayer)) return false
            sendMsg(sender, "fly.self.success")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
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
        val flyConfig = getCommandsConfig().commands.fly
        return if (flyConfig.flyDisabledWorlds.contains(target.world.dimName())) {
            !target.hasPermissionLevel(flyConfig.disabledWorldsBypassPermLevel)
        } else {
            false
        }
    }
}
