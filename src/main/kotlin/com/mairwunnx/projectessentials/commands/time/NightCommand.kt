package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object NightCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.night

    init {
        command = "night"
        time = 13000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.night
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
