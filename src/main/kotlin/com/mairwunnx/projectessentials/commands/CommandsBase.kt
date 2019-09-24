package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

@UnstableDefault
class CommandsBase {
    private val logger = LogManager.getLogger()

    fun registerAll(
        commandDispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("Command registering is starting ...")
        HealCommand.register(commandDispatcher)
        FeedCommand.register(commandDispatcher)
        TopCommand.register(commandDispatcher)
        AirCommand.register(commandDispatcher)
        FlyCommand.register(commandDispatcher)
        GodCommand.register(commandDispatcher)
        ListCommand.register(commandDispatcher)
        BreakCommand.register(commandDispatcher)
        GetPosCommand.register(commandDispatcher)
        MoreCommand.register(commandDispatcher)
        DayCommand.register(commandDispatcher)
        NightCommand.register(commandDispatcher)
        MidnightCommand.register(commandDispatcher)
        NoonCommand.register(commandDispatcher)
        SunsetCommand.register(commandDispatcher)
        SunriseCommand.register(commandDispatcher)
        TimeCommand.register(commandDispatcher)
        EssentialsCommand.register(commandDispatcher)
    }
}
