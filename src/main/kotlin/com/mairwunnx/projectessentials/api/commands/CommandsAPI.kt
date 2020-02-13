@file:Suppress("unused")

package com.mairwunnx.projectessentials.api.commands

import com.mairwunnx.projectessentials.ProjectEssentials
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.extensions.playerName
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

abstract class CommandsAPI(
    val commandName: String,
    var aliases: List<String> = emptyList(),
    var literal: LiteralArgumentBuilder<CommandSource> = literal<CommandSource>(commandName)
) {
    private val logger = LogManager.getLogger()
    private var onCommandExecute: ((CommandContext<CommandSource>) -> Int)? = null

    fun onCommandExecute(
        function: (CommandContext<CommandSource>) -> Int
    ) {
        onCommandExecute = function
    }

    init {
        logger.info("Initializing $commandName command")
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Starting registering $commandName command")
        registerAliases()

        val literalNode =
            dispatcher.register(literal.executes(::process))

        aliases.forEach {
            if (it != commandName) {
                dispatcher.register(
                    Commands.literal(it).executes(::process).redirect(literalNode)
                )
            }
        }
    }

    private fun registerAliases() {
        if (!ProjectEssentials.cooldownsInstalled) return
        CommandsAliases.aliases[commandName] = aliases.toMutableList()
        logger.debug("Registered aliases for command $commandName is $aliases")
    }

    private fun process(
        context: CommandContext<CommandSource>
    ): Int {
        logger.info("Executing $commandName command by ${context.playerName()}")
        onCommandExecute?.invoke(context) ?: logger.error(
            "Action for command $commandName not performed"
        )
        logger.info("Command $commandName executed by ${context.playerName()}")
        return 0
    }

    fun getStringExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        StringArgumentType.getString(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getString(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): String = StringArgumentType.getString(context, argumentName)

    fun getBoolExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        BoolArgumentType.getBool(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getBool(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = BoolArgumentType.getBool(context, argumentName)

    fun getEntityExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getEntity(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getEntity(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Entity = EntityArgument.getEntity(context, argumentName)

    fun getEntitiesExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getEntities(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getEntities(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): MutableCollection<out Entity> = EntityArgument.getEntities(context, argumentName)

    fun getPlayerExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getPlayer(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getPlayer(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): ServerPlayerEntity = EntityArgument.getPlayer(context, argumentName)

    fun getPlayersExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getPlayers(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun getPlayers(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): MutableCollection<ServerPlayerEntity> = EntityArgument.getPlayers(context, argumentName)
}
