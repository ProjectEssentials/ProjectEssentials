package com.mairwunnx.projectessentials.commands

object CommandAliases {
    /**
     * Where String - command for aliases.
     * Where MutableList<String> - aliases of command.
     */
    val aliases: HashMap<String, MutableList<String>> = hashMapOf()

    fun searchForAliases(
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
}
