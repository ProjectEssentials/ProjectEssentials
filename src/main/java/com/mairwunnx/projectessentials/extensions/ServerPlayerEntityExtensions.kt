@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.mairwunnx.projectessentials.extensions

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent

fun ServerPlayerEntity.sendMessage(msg: String) {
    this.sendMessage(ITextComponent.Serializer.fromJson("{\"text\":\"$msg\"}"))
}
