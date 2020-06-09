package com.mairwunnx.projectessentials

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.FoodStats

/**
 * @return server player instance by nickname. Return null
 * if player not exist.
 */
fun MinecraftServer.findPlayer(nickname: String): ServerPlayerEntity? =
    playerList.getPlayerByUsername(nickname)

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
