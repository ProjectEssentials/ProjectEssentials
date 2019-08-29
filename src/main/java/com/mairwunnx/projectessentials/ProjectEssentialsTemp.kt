package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.proxy.ClientProxy
import com.mairwunnx.projectessentials.proxy.CommonProxy
import com.mairwunnx.projectessentials.proxy.ServerProxy
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Supplier

const val MOD_ID = "projectessentials"

@Mod(MOD_ID)
class ProjectEssentialsTemp {
    private val logger: Logger = LogManager.getLogger()
    private val proxy: CommonProxy =
        DistExecutor.runForDist<CommonProxy>(
            { Supplier { ClientProxy() } },
            { Supplier { ServerProxy() } }
        )

    init {
        FMLKotlinModLoadingContext.get().modEventBus.register(proxy)
    }
}
