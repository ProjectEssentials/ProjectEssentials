package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.ProjectEssentials
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.extensions.empty
import com.mairwunnx.projectessentials.core.extensions.isPlayerSender
import com.mairwunnx.projectessentials.helpers.validateAlias
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

abstract class CommandBase {
    private val logger = LogManager.getLogger()
    lateinit var sender: CommandSource
    lateinit var senderName: String
    lateinit var senderPlayer: ServerPlayerEntity
    lateinit var target: CommandSource
    lateinit var targetName: String
    lateinit var targetPlayer: ServerPlayerEntity
    var targetIsExists = false
    var senderIsServer = false
    var command: String = String.empty
    var aliases: MutableList<String> = mutableListOf()
    private val commandAliases: MutableList<String> = mutableListOf()

    open fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Register \"/$command\" command")
        assignCommandAliases()
        applyCommandAliases()
    }

    private fun assignCommandAliases() {
        try {
            aliases.add(command)
            aliases.forEach {
                when {
                    validateAlias(it) -> commandAliases.add(it)
                    else -> logger.error("Alias assigning $it skipped: alias validation fail!")
                }
            }
        } catch (ex: ConcurrentModificationException) {
            logger.error(
                "an error occurred while assigning command aliases, please report it on github issues.",
                ex
            )
        }
    }

    private fun applyCommandAliases() {
        if (!ProjectEssentials.cooldownsInstalled) return
        CommandsAliases.aliases[command] = commandAliases
    }

    protected open fun execute(
        c: CommandContext<CommandSource>, argument: Any? = null
    ): Int {
        if (c.isPlayerSender()) {
            sender = c.source
            senderPlayer = sender.asPlayer()
            senderName = senderPlayer.name.string
            target = c.source
            targetPlayer = target.asPlayer()
            targetName = targetPlayer.name.string
            targetIsExists = false
            senderIsServer = false
        } else {
            senderName = "server"
            senderIsServer = true
        }

        // Basically it check needed for getting target existing.
        if (argument is Boolean && argument) {
            try {
                targetPlayer = EntityArgument.getPlayer(
                    c, "player"
                )
                target = targetPlayer.commandSource
                targetName = targetPlayer.name.string
                targetIsExists = true
            } catch (ex: IllegalArgumentException) {
                logger.warn("Unnable get player instance command context.")
            }
        }
        return 0
    }

    open fun reload() {
        logger.info("Reloading \"/$command\" command")
        assignCommandAliases()
        applyCommandAliases()
    }
}
