package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object SunsetCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.sunset

    init {
        command = "sunset"
        time = 12000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.sunset
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
