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
        try {
            aliases.keys.forEach { baseCommand ->
                val aliasesOfCommands = aliases[baseCommand]
                if (aliasesOfCommands != null) {
                    if (aliasesOfCommands.contains(command)) {
                        return Pair(
                            cooldownsMap[baseCommand], baseCommand
                        )
                    }
                }
            }
            return Pair(null, "")
        } catch (ex: KotlinNullPointerException) {
            return Pair(null, "")
        }
    }
}
