package com.mairwunnx.projectessentials.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource

class CommandsBase {
    @UseExperimental(UnstableDefault::class)
    fun registerAll(
        commandDispatcher: CommandDispatcher<CommandSource>
    ) {
        HealCommand.register(commandDispatcher)
        FeedCommand.register(commandDispatcher)
        TopCommand.register(commandDispatcher)
        AirCommand.register(commandDispatcher)
        FlyCommand.register(commandDispatcher)
        EssentialsCommand.register(commandDispatcher)
    }
}
