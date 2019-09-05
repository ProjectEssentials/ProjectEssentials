package com.mairwunnx.projectessentials.extensions

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

fun CommandContext<CommandSource>.isPlayerSender(): Boolean =
    this.source.entity is ServerPlayerEntity
