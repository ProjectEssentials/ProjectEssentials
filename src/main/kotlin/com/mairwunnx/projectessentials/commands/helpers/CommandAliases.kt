package com.mairwunnx.projectessentials.commands.helpers

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentialscore.extensions.empty
import net.minecraft.util.Tuple

object CommandAliases {
    /**
     * Where String - command for aliases.
     * Where MutableList<String> - aliases of command.
     */
    val aliases: HashMap<String, MutableList<String>> = hashMapOf()

    fun searchForAliasesForCooldown(
        command: String,
        cooldownsMap: HashMap<String, Int>
    ): Tuple<Int?, String> {
        return try {
            aliases.keys.forEach { baseCommand ->
                val aliasesOfCommands = aliases[baseCommand]
                if (aliasesOfCommands != null &&
                    aliasesOfCommands.contains(command)
                ) {
                    Tuple(cooldownsMap[baseCommand]!!, baseCommand)
                }
            }
            Tuple(0, String.empty)
        } catch (ex: KotlinNullPointerException) {
            Tuple(0, String.empty)
        }
    }

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
