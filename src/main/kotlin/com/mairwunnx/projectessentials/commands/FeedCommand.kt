package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.empty
import com.mairwunnx.projectessentials.extensions.isNeedFood
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
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
import org.apache.logging.log4j.Logger
import java.lang.reflect.Field

/**
 * **Description:** Satisfy the hunger of you or the given player.
 *
 * **Usage example:** `/feed`, `/eat`, `/eeat` and `/efeed`.
 *
 * **Available arguments:** &#91`player`&#93 - command executing
 * target.
 *
 * **Can server use it command?:** `false`.
 */
@UnstableDefault
object FeedCommand {
    private val logger: Logger = LogManager.getLogger()
    private const val FEED_COMMAND: String = "feed"
    private const val FEED_ARG_NAME_COMMAND: String = "player"
    private var maxSaturateLevel = 5.0f
    private var maxFoodLevel = 20
    private val feedCommandAliases: MutableList<String> = mutableListOf(FEED_COMMAND)
    private val saturationLevel: Field by lazy {
        return@lazy ObfuscationReflectionHelper.findField(
            FoodStats::class.java,
            "field_75125_b"
        )
    }

    fun register(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        logger.info("    - register \"/$FEED_COMMAND\" command ...")

        CommandAliases.aliases[FEED_COMMAND] = modConfig.commands.feed.aliases.toMutableList()
        feedCommandAliases.addAll(modConfig.commands.feed.aliases)

        feedCommandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument(
                        FEED_ARG_NAME_COMMAND,
                        EntityArgument.player()
                    ).executes {
                        execute(it, true)
                        return@executes 1
                    }
                )
                .executes {
                    execute(it)
                    return@executes 1
                }
            )
        }
    }

    private fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean = false
    ) {
        val modConfig = ModConfiguration.getCommandsConfig()
        val permissionLevel = modConfig.commands.feed.permissionLevel
        val argUsePermissionLevel = modConfig.commands.feed.argUsePermissionLevel
        val isEnabledArgs = modConfig.commands.feed.enableArgs
        maxFoodLevel = modConfig.commands.feed.maxFoodLevel
        maxSaturateLevel = modConfig.commands.feed.maxFoodSaturationLevel
        val isPlayerSender = c.isPlayerSender()
        lateinit var targetAsPlayer: ServerPlayerEntity
        var playerNickNameAsTarget: String = String.empty

        if (!isPlayerSender) {
            logger.warn(ONLY_PLAYER_CAN.replace("%0", FEED_COMMAND))
            return
        }

        if (hasTarget) {
            targetAsPlayer = EntityArgument.getPlayer(
                c, FEED_ARG_NAME_COMMAND
            )
            playerNickNameAsTarget = targetAsPlayer.name.string
        }

        val commandSender = c.source
        val commandSenderPlayer = commandSender.asPlayer()
        val commandSenderNickName = commandSenderPlayer.name.string

        if (hasTarget && !isEnabledArgs) {
            logger.warn(
                DISABLED_COMMAND_ARG
                    .replace("%0", commandSenderNickName)
                    .replace("%1", FEED_COMMAND)
            )
            sendMsg(commandSender, "common.arg.error", FEED_COMMAND)
            return
        }

        if (!commandSenderPlayer.hasPermissionLevel(permissionLevel)) {
            logger.warn(
                PERMISSION_LEVEL
                    .replace("%0", commandSenderNickName)
                    .replace("%1", FEED_COMMAND)
            )
            if (hasTarget) {
                sendMsg(commandSender, "feed.player.error", playerNickNameAsTarget)
            } else {
                sendMsg(commandSender, "feed.self.error")
            }
            return
        }

        if (hasTarget) {
            if (!commandSenderPlayer.hasPermissionLevel(argUsePermissionLevel)) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", commandSenderNickName)
                        .replace("%1", FEED_COMMAND)
                )
                sendMsg(commandSender, "feed.player.error", playerNickNameAsTarget)
                return
            }

            if (!targetAsPlayer.foodStats.isNeedFood()) {
                sendMsg(commandSender, "feed.player.maxfeed", playerNickNameAsTarget)
                return
            }
            logger.info(
                "Player ($playerNickNameAsTarget) food level/saturation changed from ${targetAsPlayer.foodStats.foodLevel}/${targetAsPlayer.foodStats.saturationLevel} to 20/5.0 by $commandSenderNickName"
            )
            targetAsPlayer.foodStats.foodLevel = maxFoodLevel
            saturateTarget(targetAsPlayer)

            sendMsg(commandSender, "feed.player.success", playerNickNameAsTarget)
            sendMsg(
                targetAsPlayer.commandSource,
                "feed.player.recipient.success",
                commandSenderNickName
            )
        } else {
            if (!commandSenderPlayer.foodStats.isNeedFood()) {
                sendMsg(commandSender, "feed.self.maxfeed")
                return
            }
            logger.info(
                "Player ($commandSenderNickName) food level/saturation changed from ${commandSender.asPlayer().foodStats.foodLevel}/${commandSender.asPlayer().foodStats.saturationLevel} to 20/5.0"
            )
            commandSender.asPlayer().foodStats.foodLevel = maxFoodLevel
            saturateTarget(commandSenderPlayer)
            sendMsg(commandSender, "feed.self.success")
        }

        logger.info("Executed command \"/$FEED_COMMAND\" from $commandSenderNickName")
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
