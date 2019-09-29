package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
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
object MoreCommand : CommandBase<CommandsConfig.Commands.More>(
    getCommandsConfig().commands.more,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.more
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
        if (item.count < item.maxStackSize) {
            item.count = item.maxStackSize
            sendMsg(sender, "more.out")
        } else {
            sendMsg(sender, "more.fullstack")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
