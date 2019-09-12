package com.mairwunnx.projectessentials.helpers

import com.mairwunnx.projectessentials.enums.ForgeRootPaths
import java.io.File
import net.minecraft.client.Minecraft

private val clientRootDir by lazy {
    Minecraft.getInstance().gameDir.absolutePath
}
private val serverRootDir by lazy {
    File(".").absolutePath
}

fun getRootPath(pathType: ForgeRootPaths): String {
    return when (pathType) {
        ForgeRootPaths.CLIENT -> clientRootDir
        ForgeRootPaths.SERVER -> serverRootDir
    }
}
