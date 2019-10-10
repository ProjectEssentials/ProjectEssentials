package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object MidnightCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.midNight

    init {
        command = "midnight"
        time = 18000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.midNight
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
