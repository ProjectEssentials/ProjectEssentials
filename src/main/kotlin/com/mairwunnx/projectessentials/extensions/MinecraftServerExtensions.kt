package com.mairwunnx.projectessentials.extensions

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer

/**
 * @return server player instance by nickname. Return null
 * if player not exist.
 */
fun MinecraftServer.findPlayer(nickname: String): ServerPlayerEntity? =
    playerList.getPlayerByUsername(nickname)
