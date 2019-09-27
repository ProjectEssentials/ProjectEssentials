package com.mairwunnx.projectessentials.commands.health

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.isNeedFood
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.FoodStats
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.apache.logging.log4j.LogManager

@UnstableDefault
object FeedCommand : CommandBase<CommandsConfig.Commands.Feed>(
    getCommandsConfig().commands.feed
) {
    private val logger = LogManager.getLogger()
    private var maxSaturateLevel = 5.0f
    private var maxFoodLevel = 20
    private val saturationLevel by lazy {
        return@lazy ObfuscationReflectionHelper.findField(
            FoodStats::class.java,
            "field_75125_b"
        )
    }

    override fun reload() {
        commandInstance = getCommandsConfig().commands.feed
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        commandArgName, EntityArgument.player()
                    ).executes {
                        execute(it, true)
                        return@executes 0
                    }
                )
                .executes {
                    execute(it)
                    return@executes 0
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean
    ): Boolean {
        val code = super.execute(c, hasTarget)
        if (!code) return false

        maxFoodLevel = config.commands.feed.maxFoodLevel
        maxSaturateLevel = config.commands.feed.maxFoodSaturationLevel

        if (hasTarget) {
            if (!targetPlayer.foodStats.isNeedFood()) {
                if (senderNickName == "server") {
                    logger.info("Player $targetPlayerName appetite already fully sated.")
                } else {
                    sendMsg(sender, "feed.player.maxfeed", targetPlayerName)
                }
                return false
            }
            logger.info(
                "Player ($targetPlayerName) food level/saturation changed from ${targetPlayer.foodStats.foodLevel}/${targetPlayer.foodStats.saturationLevel} to 20/5.0 by $senderNickName"
            )
            targetPlayer.foodStats.foodLevel =
                maxFoodLevel
            saturateTarget(targetPlayer)
            if (senderNickName == "server") {
                logger.info("You satiated the appetite of player $targetPlayerName.")
            } else {
                sendMsg(sender, "feed.player.success", targetPlayerName)
            }
            sendMsg(
                targetPlayer.commandSource,
                "feed.player.recipient.success",
                senderNickName
            )
        } else {
            if (!senderPlayer.foodStats.isNeedFood()) {
                sendMsg(sender, "feed.self.maxfeed")
                return false
            }
            logger.info(
                "Player ($senderNickName) food level/saturation changed from ${senderPlayer.foodStats.foodLevel}/${senderPlayer.foodStats.saturationLevel} to 20/5.0"
            )
            senderPlayer.foodStats.foodLevel =
                maxFoodLevel
            saturateTarget(senderPlayer)
            sendMsg(sender, "feed.self.success")
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
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
