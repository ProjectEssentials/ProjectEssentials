package com.mairwunnx.projectessentials.helpers

import com.mairwunnx.projectessentials.MOD_NAME
import com.mairwunnx.projectessentials.enums.ForgeRootPaths
import java.io.File
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor

val CONFIG_FOLDER = root + File.separator + "config"
val MOD_CONFIG_FOLDER = CONFIG_FOLDER + File.separator + MOD_NAME
val COOLDOWNS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "cooldowns.json"
val COMMANDS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "commands.json"

private val root: String
    get() {
        var rootPath = ""
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                rootPath = getRootPath(ForgeRootPaths.CLIENT)
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                rootPath = getRootPath(ForgeRootPaths.SERVER)
            }
        }
        return rootPath
    }
