package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration

object CommandAliases {
    /**
     * Where String - command for aliases.
     * Where MutableList<String> - aliases of command.
     */
    val aliases: HashMap<String, MutableList<String>> = hashMapOf()

    fun searchForAliasesForCooldown(
        command: String,
        cooldownsMap: HashMap<String, Int>
    ): Pair<Int?, String> {
        return try {
            aliases.keys.forEach { baseCommand ->
                val aliasesOfCommands = aliases[baseCommand]
                if (aliasesOfCommands != null &&
                    aliasesOfCommands.contains(command)
                ) {
                    return cooldownsMap[baseCommand] to baseCommand
                }
            }
            null to ""
        } catch (ex: KotlinNullPointerException) {
            null to ""
        }
    }

    /**
     * Return true if command argument is alias of
     * blocked command.
     */
    fun searchForAliases(command: String): Boolean {
        val modConfig = ModConfiguration.getCommandsConfig()
        aliases.keys.forEach { baseCommand ->
            val aliasesOfCommands = aliases[baseCommand] // heal
            if (aliasesOfCommands != null &&
                aliasesOfCommands.contains(command) &&
                modConfig.disabledCommands.contains(baseCommand)
            ) return true
        }
        return false
    }
}
