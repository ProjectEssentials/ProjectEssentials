package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

private val generalConfiguration = getConfigurationByName<GeneralConfiguration>("general")

/**
 * Return true if command argument is alias of
 * blocked command.
 */
fun isAliasesOfBlockedCommand(command: String): Boolean {
    CommandAliases.aliases.keys.forEach {
        if (
            CommandAliases.aliases[it]?.contains(command) == true &&
            it in generalConfiguration.getList(SETTING_DISABLED_COMMANDS)
        ) return true
    }
    return false
}

fun validateAndExecute(
    context: CommandContext<CommandSource>,
    node: String,
    opLevel: Int,
    action: (isServer: Boolean) -> Unit
) = context.getPlayer()?.let {
    if (hasPermission(it, node, opLevel)) {
        action(false)
    } else {
        MessagingAPI.sendMessage(
            context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}basic.restricted"
        )
    }
} ?: run { action(true) }
