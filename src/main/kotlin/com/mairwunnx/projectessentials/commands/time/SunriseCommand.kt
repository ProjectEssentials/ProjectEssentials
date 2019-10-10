package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object SunriseCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.sunrise

    init {
        command = "sunrise"
        time = 23000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.sunrise
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
