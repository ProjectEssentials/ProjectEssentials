package com.mairwunnx.projectessentials.commands.weather

import com.mairwunnx.projectessentials.commands.CommandWeatherBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object SunCommand : CommandWeatherBase() {
    private var config = getCommandsConfig().commands.sun

    init {
        command = "sun"
        weather = Weather.CLEAR
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.sun
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
