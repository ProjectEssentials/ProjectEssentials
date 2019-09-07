package com.mairwunnx.projectessentials.extensions

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Return true if command sender is player.
 */
fun CommandContext<CommandSource>.isPlayerSender(): Boolean =
    this.source.entity is ServerPlayerEntity
