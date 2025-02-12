package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.SETTING_FLY_WORLDS_DISABLED
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.generalConfiguration
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

object FlyCommand : CommandBase(flyLiteral, false) {
    override val name = "fly"

    fun flySelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.fly.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    when {
                        !validateWorld(this) -> MessagingAPI.sendMessage(
                            this, "${MESSAGE_MODULE_PREFIX}basic.fly.self.world_restricted"
                        )
                        !validateMode(this) -> MessagingAPI.sendMessage(
                            this, "${MESSAGE_MODULE_PREFIX}basic.fly.self.mode_restricted"
                        )
                        else -> {
                            switchFly(this)
                            MessagingAPI.sendMessage(
                                this, "${MESSAGE_MODULE_PREFIX}basic.fly.self.success"
                            ).also { process(context) }
                        }
                    }
                }
            }
        }
    }

    fun flyOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.fly.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                if (validateWorld(player) && validateMode(player)) {
                    switchFly(player)
                    MessagingAPI.sendMessage(
                        player,
                        "${MESSAGE_MODULE_PREFIX}basic.fly.by.success",
                        args = *arrayOf(context.playerName())
                    )
                }
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        if (!validateMode(players.first())) {
                            "Can't change fly mode for player ${players.first().name.string}, game mode used by the player is prohibited for flights."
                        } else if (!validateWorld(players.first())) {
                            "Can't change fly mode for player ${players.first().name.string}, world in which the player is prohibited for flights."
                        } else {
                            "You've changed fly mode for player ${players.first().name.string}"
                        }
                    } else {
                        "You've changed fly mode for selected (${players.count()}) players"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        if (!validateMode(players.first())) {
                            "${MESSAGE_MODULE_PREFIX}basic.fly.other_single.mode_restricted"
                        } else if (!validateWorld(players.first())) {
                            "${MESSAGE_MODULE_PREFIX}basic.fly.other_single.world_restricted"
                        } else {
                            "${MESSAGE_MODULE_PREFIX}basic.fly.other_single.success"
                        }
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.fly.other_multiple.success"
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

    fun validateWorld(player: ServerPlayerEntity) =
        if (hasPermission(player, "ess.fly.world.bypass", 4)) true
        else player.currentDimensionName !in generalConfiguration.getList(
            SETTING_FLY_WORLDS_DISABLED
        )

    fun validateMode(player: ServerPlayerEntity) = !player.isCreative && !player.isSpectator

    fun switchFly(target: ServerPlayerEntity) {
        with(target.abilities) {
            allowEdit = true
            if (target.onGround) {
                allowFlying = !allowFlying
                isFlying = !allowFlying
            } else {
                allowFlying = !isFlying
                isFlying = !isFlying
            }
        }
        target.sendPlayerAbilities()
    }
}
