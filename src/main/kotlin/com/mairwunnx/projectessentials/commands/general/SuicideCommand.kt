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
import net.minecraft.util.DamageSource
import org.apache.logging.log4j.LogManager

@UnstableDefault
object SuicideCommand : CommandBase<CommandsConfig.Commands.Suicide>(
    getCommandsConfig().commands.suicide,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.suicide
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

        senderPlayer.attackEntityFrom(
            DamageSource("suicide")
                .setDamageBypassesArmor()
                .setDamageAllowedInCreativeMode(),
            Float.MAX_VALUE
        )
        sendMsg(sender, "suicide.success")

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
