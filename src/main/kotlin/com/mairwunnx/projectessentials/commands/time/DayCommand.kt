package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object DayCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.day

    init {
        command = "day"
        time = 1000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.day
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
