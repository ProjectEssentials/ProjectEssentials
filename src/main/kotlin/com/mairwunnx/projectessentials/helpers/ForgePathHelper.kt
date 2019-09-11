package com.mairwunnx.projectessentials.helpers

import java.io.File
import net.minecraft.client.Minecraft

enum class ForgeRootPaths { CLIENT, SERVER }

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
