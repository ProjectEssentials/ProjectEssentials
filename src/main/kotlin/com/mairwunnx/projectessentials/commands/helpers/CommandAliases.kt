package com.mairwunnx.projectessentials.commands.helpers

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases.aliases

object CommandAliases {
    /**
     * Return true if command argument is alias of
     * blocked command.
     */
    fun searchForAliases(command: String): Boolean {
        val modConfig = ModConfiguration.getCommandsConfig()
        aliases.keys.forEach { baseCommand ->
            val aliasesOfCommands = aliases[baseCommand]
            if (aliasesOfCommands != null &&
                aliasesOfCommands.contains(command) &&
                modConfig.disabledCommands.contains(baseCommand)
            ) return true
        }
        return false
    }
}
