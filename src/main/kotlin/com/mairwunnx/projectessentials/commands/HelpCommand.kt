package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.SETTING_HELP_COMMAND_COLORIZED_OUT
import com.mairwunnx.projectessentials.SETTING_REPLACE_NATIVE_HELP_COMMAND
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.CommandSource

object HelpCommand : CommandBase(helpLiteral) {
    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    override val name = "help"
    override val override by lazy {
        generalConfiguration.getBool(SETTING_REPLACE_NATIVE_HELP_COMMAND)
    }

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "native.help", 0) { isServer ->
            val map = CommandAPI.getDispatcher().getSmartUsage(
                CommandAPI.getDispatcher().root, context.source
            )
            val message =
                if (generalConfiguration.getBool(SETTING_HELP_COMMAND_COLORIZED_OUT)) {
                    if (isServer) map.values.toList() else highlight(map)
                } else map.values.toList()

            if (isServer) {
                ServerMessagingAPI.listAsResponse(message) { "Help" }
            } else {
                MessagingAPI.sendListAsMessage(context, message) { "Help" }
                super.process(context)
            }
        }
    }

    private fun highlight(map: Map<CommandNode<CommandSource>, String>): List<String> {
        return map.values.map {
            "/$it".replace("/", "§7/")
                .replace(" ", "§7 ")
                .replace("(", "§8(§c")
                .replace("|", "§8|§c")
                .replace(")", "§8)§7")
                .replace("[", "§8[§c")
                .replace("]", "§8]§7")
                .replace("<", "§8<§c")
                .replace(">", "§8>§7")
                .replace("-§8>§7", "§d->§7")
        }
    }
}
