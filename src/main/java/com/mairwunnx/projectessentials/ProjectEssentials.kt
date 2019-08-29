package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.proxy.ClientProxy
import com.mairwunnx.projectessentials.proxy.CommonProxy
import com.mairwunnx.projectessentials.proxy.ServerProxy
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Supplier

const val MOD_ID = "projectessentials"
const val MOD_NAME = "Project Essentials"

@Mod(MOD_ID)
class ProjectEssentials {
    private val logger: Logger = LogManager.getLogger()
    private val proxy: CommonProxy =
        DistExecutor.runForDist<CommonProxy>(
            { Supplier { ClientProxy() } },
            { Supplier { ServerProxy() } }
        )

    init {
        logger.info("$MOD_NAME starting initializing ...")
        FMLKotlinModLoadingContext.get().modEventBus.register(proxy)
        FMLKotlinModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup()
        }
    }

    private fun setup() {
        logger.info("$MOD_NAME starting setup mod ...")
    }
}
