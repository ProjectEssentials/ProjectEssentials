package com.mairwunnx.projectessentials.commands.time

import com.mairwunnx.projectessentials.commands.CommandTimeBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object NoonCommand : CommandTimeBase() {
    private var config = getCommandsConfig().commands.noon

    init {
        command = "noon"
        time = 6000
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.noon
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
