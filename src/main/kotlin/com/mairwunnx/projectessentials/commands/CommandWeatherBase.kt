package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.commands.weather.Weather
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

abstract class CommandWeatherBase : CommandBase() {
    private val logger = LogManager.getLogger()
    var weather = Weather.CLEAR
    var defaultDuration = 6000

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        "duration",
                        IntegerArgumentType.integer()
                    ).executes {
                        execute(it, IntegerArgumentType.getInteger(it, "duration"))
                        return@executes 0
                    }
                )
                .executes {
                    return@executes execute(it)
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)
        val durationExist = argument is Int && argument > 0
        val duration by lazy {
            when {
                durationExist -> return@lazy argument as Int
                else -> return@lazy defaultDuration
            }
        }

        if (senderIsServer) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            return 0
        } else {
            if (hasPermission()) {
                sender.world.worldInfo.clearWeatherTime = 0
                if (weather == Weather.CLEAR) {
                    sender.world.worldInfo.thunderTime = 0
                    sender.world.worldInfo.rainTime = 0
                    sender.world.worldInfo.isRaining = false
                    sender.world.worldInfo.isThundering = false
                }
                if (weather == Weather.RAIN) {
                    if (durationExist) {
                        sender.world.worldInfo.rainTime = duration
                        sender.world.worldInfo.thunderTime = 0
                        sender.world.worldInfo.isRaining = true
                        sender.world.worldInfo.isThundering = false
                    } else {
                        sender.world.worldInfo.rainTime = defaultDuration
                        sender.world.worldInfo.thunderTime = 0
                        sender.world.worldInfo.isRaining = true
                        sender.world.worldInfo.isThundering = false
                    }
                }
                if (weather == Weather.THUNDER) {
                    if (durationExist) {
                        sender.world.worldInfo.thunderTime = duration
                        sender.world.worldInfo.rainTime = duration
                        sender.world.worldInfo.isRaining = true
                        sender.world.worldInfo.isThundering = true
                    } else {
                        sender.world.worldInfo.thunderTime = defaultDuration
                        sender.world.worldInfo.rainTime = defaultDuration
                        sender.world.worldInfo.isRaining = true
                        sender.world.worldInfo.isThundering = true
                    }
                }
                sendMsg(sender, "$command.installed")
            } else {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderName)
                        .replace("%1", command)
                )
                sendMsg(sender, "weather.restricted", senderName)
                return 0
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }

    fun hasPermission(): Boolean {
        return (PermissionsAPI.hasPermission(senderName, "ess.$command") ||
                PermissionsAPI.hasPermission(senderName, "ess.weather"))
    }
}
