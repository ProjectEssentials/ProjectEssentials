package com.mairwunnx.projectessentials.helpers

import kotlin.math.floor

const val DAY = 24000L
const val TICKS_AT_MIDNIGHT = 18000
const val TICKS_PER_DAY = 24000
const val TICKS_PER_HOUR = 1000
const val TICKS_PER_MINUTE = 1000.0 / 60.0

/**
 * Thanks Essentials plugin sources for this.
 *
 * **https://github.com/EssentialsX/Essentials/blob/
 * dbb9757a9f9a10fb35db4773416a1d6f707c06db/Essentials/src/com/
 * earth2me/essentials/utils/DescParseTickFormat.java#L212**
 */
fun get24TimeFromTicks(ticks: Long): String {
    return when (ticks) {
        0L -> "06:00"
        DAY -> "00:00"
        else -> {
            var mutableTicks = ticks
            mutableTicks = mutableTicks - TICKS_AT_MIDNIGHT + TICKS_PER_DAY

            val days = mutableTicks / TICKS_PER_DAY
            mutableTicks -= days * TICKS_PER_DAY

            val hours = mutableTicks / TICKS_PER_HOUR
            mutableTicks -= hours * TICKS_PER_HOUR

            val minutes = floor(mutableTicks / TICKS_PER_MINUTE).toLong()

            val hoursFormatted = when {
                hours.toInt().toString().length < 2 -> "0$hours"
                else -> hours.toString()
            }
            val minutesFormatted = when {
                minutes.toInt().toString().length < 2 -> "0$minutes"
                else -> minutes.toString()
            }
            return "${hoursFormatted}:${minutesFormatted}"
        }
    }
}
