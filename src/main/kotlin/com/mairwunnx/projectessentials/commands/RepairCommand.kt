package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.util.Hand
import org.apache.logging.log4j.LogManager

@UnstableDefault
object RepairCommand : CommandBase<CommandsConfig.Commands.Repair>(
    getCommandsConfig().commands.repair,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.repair
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
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

        val item = senderPlayer.getHeldItem(Hand.MAIN_HAND)
        if (item.isDamaged) {
            item.damage = 0
            sendMsg(sender, "repair.out")
        } else {
            sendMsg(sender, "repair.fulldamage")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
