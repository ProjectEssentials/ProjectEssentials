package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.SETTING_GOD_WORLDS_DISABLED
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

object GodCommand : CommandBase(godLiteral, false) {
    override val name = "god"

    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }

    fun godSelf(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.god.self", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                with(context.getPlayer()!!) {
                    when {
                        !validateWorld(this) -> MessagingAPI.sendMessage(
                            this, "${MESSAGE_MODULE_PREFIX}basic.god.self.world_restricted"
                        )
                        !validateMode(this) -> MessagingAPI.sendMessage(
                            this, "${MESSAGE_MODULE_PREFIX}basic.god.self.mode_restricted"
                        )
                        else -> {
                            switchGod(this)
                            MessagingAPI.sendMessage(
                                this, "${MESSAGE_MODULE_PREFIX}basic.god.self.success"
                            ).also { process(context) }
                        }
                    }
                }
            }
        }
    }

    fun godOther(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.god.other", 3) { isServer ->
            val players = CommandAPI.getPlayers(context, "targets")
            players.forEach { player ->
                if (validateWorld(player) && validateMode(player)) {
                    switchGod(player)
                    MessagingAPI.sendMessage(
                        player,
                        "${MESSAGE_MODULE_PREFIX}basic.god.by.success",
                        args = *arrayOf(context.playerName())
                    )
                }
            }
            if (isServer) {
                ServerMessagingAPI.response {
                    if (players.count() == 1) {
                        if (!validateMode(players.first())) {
                            "Can't change god mode for player ${players.first().name.string}, game mode used by the player is prohibited for god mode."
                        } else if (!validateWorld(players.first())) {
                            "Can't change god mode for player ${players.first().name.string}, world in which the player is prohibited for god mode."
                        } else {
                            "You've changed god mode for player ${players.first().name.string}"
                        }
                    } else {
                        "You've changed god mode for selected (${players.count()}) players"
                    }
                }
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    if (players.count() == 1) {
                        if (!validateMode(players.first())) {
                            "${MESSAGE_MODULE_PREFIX}basic.god.other_single.mode_restricted"
                        } else if (!validateWorld(players.first())) {
                            "${MESSAGE_MODULE_PREFIX}basic.god.other_single.world_restricted"
                        } else {
                            "${MESSAGE_MODULE_PREFIX}basic.god.other_single.success"
                        }
                    } else {
                        "${MESSAGE_MODULE_PREFIX}basic.god.other_multiple.success"
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
        if (hasPermission(player, "ess.god.world.bypass", 4)) true
        else player.currentDimensionName !in generalConfiguration.getList(
            SETTING_GOD_WORLDS_DISABLED
        )

    fun validateMode(player: ServerPlayerEntity) = !player.isCreative && !player.isSpectator

    fun switchGod(target: ServerPlayerEntity) {
        with(target.abilities) {
            allowEdit = true
            disableDamage = !disableDamage
        }
        target.sendPlayerAbilities()
    }
}
