package com.mairwunnx.projectessentials.commands.weather

import com.mairwunnx.projectessentials.commands.CommandWeatherBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object StormCommand : CommandWeatherBase() {
    private var config = getCommandsConfig().commands.storm

    init {
        command = "storm"
        weather = Weather.THUNDER
        defaultDuration = config.defaultDuration
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.storm
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
