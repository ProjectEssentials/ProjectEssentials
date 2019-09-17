package com.mairwunnx.projectessentials.extensions

import net.minecraft.world.World

fun World.fullName(): String =
    this.worldInfo.worldName + "&" + this.dimension.type.registryName.toString()

fun World.dimName(): String = this.dimension.type.registryName.toString()
