package com.mairwunnx.projectessentials.commands.weather

import com.mairwunnx.projectessentials.commands.CommandWeatherBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig

object RainCommand : CommandWeatherBase() {
    private var config = getCommandsConfig().commands.rain

    init {
        command = "rain"
        weather = Weather.RAIN
        defaultDuration = config.defaultDuration
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.rain
        aliases = config.aliases.toMutableList()
        super.reload()
    }
}
