package com.mairwunnx.projectessentials.helpers

import com.mairwunnx.projectessentials.MOD_NAME
import com.mairwunnx.projectessentials.MOD_TARGET_FORGE_REGEX
import com.mairwunnx.projectessentials.MOD_VERSION
import net.minecraftforge.versions.forge.ForgeVersion
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

fun validateForgeVersion() {
    logger.info("Checking forge version for compatibility with mod ...")
    if (Regex(MOD_TARGET_FORGE_REGEX).matches(ForgeVersion.getVersion())) {
        logger.info("Forge version is compatibility with mod.")
    } else {
        logger.warn("Forge version may be incompatible with $MOD_NAME $MOD_VERSION!")
        logger.warn("    - update or downgrade forge version.")
        logger.warn("    - update or downgrade mod version.")
        logger.warn("    - or just create issue on github.")
    }
}
