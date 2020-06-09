package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.isNeedFood
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.FoodStats
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.ObfuscationReflectionHelper

object FeedCommand : CommandBase(feedLiteral, false) {
    override val name = "feed"
    override val aliases = listOf("eat")

    private const val maxSaturateLevel = 5.0f
    private const val maxFoodLevel = 20

    private val saturationLevel by lazy {
        ObfuscationReflectionHelper.findField(
            FoodStats::class.java, "field_75125_b"
        )
    }

    fun feedSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.feed.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    if (this.foodStats.isNeedFood()) {
                        this.foodStats.foodLevel = maxFoodLevel
                        saturateTarget(this)
                    }
                    MessagingAPI.sendMessage(
                        this, "${MESSAGE_MODULE_PREFIX}basic.feed.self.success"
                    )
                }.also { process(context) }
            }
        }
    }

    fun feedOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.feed.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                if (player.foodStats.isNeedFood()) {
                    player.foodStats.foodLevel = maxFoodLevel
                    saturateTarget(player)
                }
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.feed.by.success",
                    args = *arrayOf(context.playerName())
                )
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        "You've saturated the ${players.first().name.string}'s appetite"
                    } else {
                        "You've saturated the selected (${players.count()}) players's appetite"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        "${MESSAGE_MODULE_PREFIX}basic.feed.other_single.success"
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.feed.other_multiple.success"
                    },
                    args = *arrayOf(
                        if (players.count() == 1) {
                            players.first().name.string
                        } else {
                            players.count().toString()
                        }
                    )
                ).also { process(context) }
            }
        }
    }

    private fun saturateTarget(target: ServerPlayerEntity) {
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable { target.foodStats.setFoodSaturationLevel(maxSaturateLevel) }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable { saturationLevel.setFloat(target.foodStats, maxSaturateLevel) }
        }
    }
}
