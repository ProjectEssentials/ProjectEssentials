package com.mairwunnx.projectessentials.cooldowns

import com.google.common.collect.HashBasedTable
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

/*
todo: move it in Project Essentials-Cooldown module.
 */
object CooldownBase {
    const val DEFAULT_COOLDOWN = 5
    const val DEFAULT_COOLDOWN_LITERAL = "*"
    private val cooldownTable = HashBasedTable.create<String, String, ZonedDateTime>()

    fun addCooldown(nickname: String, command: String) {
        if (cooldownTable.get(nickname, command) != null) {
            removeCooldown(nickname, command)
        }
        cooldownTable.put(nickname, command, ZonedDateTime.now())
    }

    @UseExperimental(ExperimentalTime::class)
    fun getCooldown(nickname: String, command: String): Double {
        if (cooldownTable.get(nickname, command) != null) {
            val commandExecutionTime = cooldownTable.get(nickname, command)
            val dateTimeNow: ZonedDateTime = ZonedDateTime.now()
            val duration = Duration.between(commandExecutionTime, dateTimeNow)
            return duration.toKotlinDuration().inSeconds
        }
        throw KotlinNullPointerException(
            "An error occurred while getting cooldown date time by nickname ($nickname) with command ($command)"
        )
    }

    fun removeCooldown(nickname: String, command: String) {
        if (cooldownTable.get(nickname, command) == null) return
        cooldownTable.remove(nickname, command)
    }

    fun getCooldownIsExpired(
        nickname: String,
        command: String,
        minSecondsDuration: Int
    ): Boolean = getCooldown(nickname, command) >= minSecondsDuration
}
