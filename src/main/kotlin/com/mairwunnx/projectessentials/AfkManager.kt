package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

object AfkManager {
    private val logger = LogManager.getLogger()

    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    private val afkPlayers = mutableSetOf<ServerPlayerEntity>()

    fun getAfkPlayers() = afkPlayers

    fun switch(player: ServerPlayerEntity, value: Boolean) {
        if (!value) {
            if (player in afkPlayers) {
                afkPlayers.remove(player)
                player.markPlayerActive()
                MessagingAPI.sendMessage(
                    player, "project_essentials.afk.disabled", args = *arrayOf(player.name.string)
                ).also {
                    logger.debug("Player ${player.name.string} afk state changed to $value")
                }
            }
        } else {
            if (player !in afkPlayers) {
                afkPlayers.add(player)
                MessagingAPI.sendMessage(
                    player, "project_essentials.afk.enabled", args = *arrayOf(player.name.string)
                ).also {
                    logger.debug("Player ${player.name.string} afk state changed to $value")
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun handle(player: ServerPlayerEntity) {
        if (generalConfiguration.getBool(SETTING_AFK_HANDLE_ACTIVITY)) {
            val lastActiveMs = player.lastActiveTime
            val nowMs = System.nanoTime() / 1000000L
            val diff = (nowMs - lastActiveMs).toDuration(TimeUnit.SECONDS).inSeconds
            if (generalConfiguration.getInt(SETTING_AFK_IDLENESS_TIME) >= diff) {
                switch(player, true)
            } else {
                switch(player, false)
            }
        }
    }
}
