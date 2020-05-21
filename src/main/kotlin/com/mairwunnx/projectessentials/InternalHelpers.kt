package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases

/**
 * Return true if command argument is alias of
 * blocked command.
 */
fun isAliasesOfBlockedCommand(command: String): Boolean {
    // todo: conf
    val modConfig = ModConfiguration.getCommandsConfig()
    CommandAliases.aliases.keys.forEach {
        if (
            CommandAliases.aliases[it]?.contains(command) == true &&
            modConfig.disabledCommands.contains(it)
        ) return true
    }
    return false
}
