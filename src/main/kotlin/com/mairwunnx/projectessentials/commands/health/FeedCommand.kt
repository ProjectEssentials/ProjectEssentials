package com.mairwunnx.projectessentials.commands.health

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.core.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.extensions.isNeedFood
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.FoodStats
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.apache.logging.log4j.LogManager

object FeedCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var maxSaturateLevel = 5.0f
    private var maxFoodLevel = 20
    private val saturationLevel by lazy {
        return@lazy ObfuscationReflectionHelper.findField(
            FoodStats::class.java,
            "field_75125_b"
        )
    }
    private var config = getCommandsConfig().commands.feed

    init {
        command = "feed"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.feed
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        "player", EntityArgument.player()
                    ).executes {
                        return@executes execute(it, true)
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

        maxFoodLevel = config.maxFoodLevel
        maxSaturateLevel = config.maxFoodSaturationLevel

        if (senderIsServer) {
            if (targetIsExists) {
                if (!targetPlayer.foodStats.isNeedFood()) {
                    logger.info("Player $targetName appetite already fully sated.")
                    return 0
                }
                logger.info(
                    "Player ($targetName) food level/saturation changed from ${targetPlayer.foodStats.foodLevel}/${targetPlayer.foodStats.saturationLevel} to 20/5.0 by $senderName"
                )
                targetPlayer.foodStats.foodLevel = maxFoodLevel
                saturateTarget(targetPlayer)
                logger.info("You satiated the appetite of player $targetName.")
                sendMsg(
                    targetPlayer.commandSource,
                    "feed.other.recipient_out",
                    senderName
                )
            } else {
                logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            }
            return 0
        } else {
            if (targetIsExists) {
                if (PermissionsAPI.hasPermission(senderName, "ess.feed.other")) {
                    when {
                        !config.enableArgs -> {
                            logger.warn(
                                DISABLED_COMMAND_ARG
                                    .replace("%0", senderName)
                                    .replace("%1", command)
                            )
                            sendMsg(sender, "common.arg.disabled", command)
                            return 0
                        }
                    }

                    if (!targetPlayer.foodStats.isNeedFood()) {
                        sendMsg(sender, "feed.other.maxfeed", targetName)
                        return 0
                    }
                    logger.info(
                        "Player ($targetName) food level/saturation changed from ${targetPlayer.foodStats.foodLevel}/${targetPlayer.foodStats.saturationLevel} to 20/5.0 by $senderName"
                    )
                    targetPlayer.foodStats.foodLevel = maxFoodLevel
                    saturateTarget(targetPlayer)
                    sendMsg(sender, "feed.other.success", targetName)
                    sendMsg(
                        target,
                        "feed.other.recipient_out",
                        senderName
                    )
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "feed.other.restricted", targetName)
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.feed")) {
                    if (!senderPlayer.foodStats.isNeedFood()) {
                        sendMsg(sender, "feed.self.maxfeed")
                        return 0
                    }
                    logger.info(
                        "Player ($senderName) food level/saturation changed from ${senderPlayer.foodStats.foodLevel}/${senderPlayer.foodStats.saturationLevel} to 20/5.0"
                    )
                    senderPlayer.foodStats.foodLevel = maxFoodLevel
                    saturateTarget(senderPlayer)
                    sendMsg(sender, "feed.self.success")
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "feed.self.restricted", senderName)
                    return 0
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }

    private fun saturateTarget(target: ServerPlayerEntity) {
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                target.foodStats.setFoodSaturationLevel(maxSaturateLevel)
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                saturationLevel.setFloat(target.foodStats, maxSaturateLevel)
            }
        }
    }
}
