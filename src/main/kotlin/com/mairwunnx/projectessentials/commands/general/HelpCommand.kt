package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.api.commands.CommandsAPI
import com.mairwunnx.projectessentials.commands.helpLiteral
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object HelpCommand : CommandsAPI(
    "help", getCommandsConfig().commands.help.aliases, helpLiteral
) {
    init {
        onCommandExecute(::execute)
    }

    fun execute(
        context: CommandContext<CommandSource>
    ): Int {
        sendMsg(context.source, "test")
        return 0
    }
}
