package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.SETTING_AFK_HANDLE_ACTIVITY
import com.mairwunnx.projectessentials.SETTING_AFK_IDLENESS_KICK_TIME
import com.mairwunnx.projectessentials.SETTING_AFK_IDLENESS_TIME
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * !Experimental, issues can be present in production.
 */
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
                player.markPlayerActive()
                afkPlayers.remove(player)
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.afk.disabled",
                    args = *arrayOf(player.name.string)
                ).also {
                    logger.debug("Player ${player.name.string} afk state changed to $value")
                }
            }
        } else {
            if (player !in afkPlayers) {
                player.playerLastActiveTime -=
                    (generalConfiguration.getInt(SETTING_AFK_IDLENESS_TIME) + 15) * 1000
                afkPlayers.add(player)
                MessagingAPI.sendMessage(
                    player,
                    "${MESSAGE_MODULE_PREFIX}basic.afk.enabled",
                    args = *arrayOf(player.name.string)
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
            switch(player, diff >= generalConfiguration.getInt(SETTING_AFK_IDLENESS_TIME))
            if (diff >= generalConfiguration.getInt(SETTING_AFK_IDLENESS_KICK_TIME)) {
                if (!hasPermission(player, "ess.afk.kick.bypass", 3)) {
                    val coreVersion = ModuleAPI.getModuleByName("core").version
                    if (coreVersion.startsWith("2.0.0") && "RC." in coreVersion) {
                        player.connection.disconnect(TextComponentUtils.toTextComponent {
                            "§cKicked by Server, reason: §7long time AFK."
                        })
                    } else {
                        player.connection.disconnect(
                            if (generalConfiguration.getBool(SETTING_LOC_ENABLED)) {
                                TextComponentUtils.toTextComponent {
                                    LocalizationAPI.getLocalizedString(
                                        LocalizationAPI.getPlayerLanguage(player),
                                        "${MESSAGE_MODULE_PREFIX}basic.afk.kicked"
                                    )
                                }
                            } else {
                                TranslationTextComponent("${MESSAGE_MODULE_PREFIX}basic.afk.kicked")
                            }
                        )
                    }
                }
            }
        }
    }
}
