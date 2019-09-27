package com.mairwunnx.projectessentials.commands.health

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

@UnstableDefault
object HealCommand : CommandBase<CommandsConfig.Commands.Heal>(
    getCommandsConfig().commands.heal
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.heal
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
                        execute(
                            it,
                            true
                        )
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
            if (!targetPlayer.shouldHeal()) {
                sendMsg(sender, "heal.player.maxhealth", targetPlayerName)
                return false
            }
            logger.info(
                "Player ($targetPlayerName) Health changed from ${targetPlayer.health} to ${targetPlayer.maxHealth} by $senderNickName"
            )
            targetPlayer.health = targetPlayer.maxHealth
            sendMsg(sender, "heal.player.success", targetPlayerName)
            sendMsg(
                targetPlayer.commandSource,
                "heal.player.recipient.success",
                targetPlayerName
            )
        } else {
            if (!senderPlayer.shouldHeal()) {
                sendMsg(sender, "heal.self.maxhealth")
                return false
            }
            logger.info(
                "Player ($senderNickName) changed from ${senderPlayer.health} to ${senderPlayer.maxHealth}"
            )
            senderPlayer.health = senderPlayer.maxHealth
            sendMsg(sender, "heal.self.success")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
