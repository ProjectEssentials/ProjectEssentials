package com.mairwunnx.projectessentials.extensions

import net.minecraft.util.FoodStats

/**
 * Return true if food level or saturation level
 * not equals max values of food level or saturation level.
 */
fun FoodStats.isNeedFood(): Boolean {
    return when {
        foodLevel < 20 -> return true
        foodLevel >= 20 && saturationLevel < 5.0f -> return true
        foodLevel >= 20 && saturationLevel >= 5.0f -> return false
        else -> false
    }
}
