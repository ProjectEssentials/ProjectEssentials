package com.mairwunnx.projectessentials.commands.moderator

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import org.apache.logging.log4j.LogManager

object ClearCommand : CommandBase() {
//    private val logger = LogManager.getLogger()
//
//    override fun reload() {
//        commandInstance = getCommandsConfig().commands.clear
//        super.reload()
//    }
//
//    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
//        super.register(dispatcher)
//        commandAliases.forEach { command ->
//            dispatcher.register(literal<CommandSource>(command)
//                .executes {
//                    execute(it)
//                    return@executes 0
//                }
//                .then(Commands.argument(
//                    "player", EntityArgument.player()
//                ).executes {
//                    execute(it, true)
//                    return@executes 0
//                })
//                .then(Commands.argument(
//                    "players", EntityArgument.players()
//                ).executes {
//                    execute(it, true)
//                    return@executes 0
//                })
//                .then(Commands.literal("allplayers").executes {
//                    execute(it, true)
//                    return@executes 0
//                })
//            )
//        }
//    }
//
//    override fun execute(
//        c: CommandContext<CommandSource>,
//        hasTarget: Boolean
//    ): Boolean {
//        val code = super.execute(c, hasTarget)
//        if (!code) return false
//
//        if (!hasTarget) {
//            senderPlayer.inventory.mainInventory.clear()
//            senderPlayer.inventory.offHandInventory.clear()
//        } else {
//            val command = c.input.split(" ")[1]
//            if (command == "allplayers") {
//                senderPlayer.server.playerList.players.forEach {
//                    it.inventory.mainInventory.clear()
//                    it.inventory.offHandInventory.clear()
//                }
//            } else {
//                val players = EntityArgument.getPlayers(c, "players")
//                if (!players.isNullOrEmpty()) {
//                    players.forEach {
//                        it.inventory.mainInventory.clear()
//                        it.inventory.offHandInventory.clear()
//                    }
//                } else {
//                    val player = EntityArgument.getPlayer(c, "player")
//                    player.inventory.mainInventory.clear()
//                    player.inventory.offHandInventory.clear()
//                }
//            }
//        }
//
//        logger.info("Executed command \"/$commandName\" from $senderNickName")
//        return true
//    }
}
