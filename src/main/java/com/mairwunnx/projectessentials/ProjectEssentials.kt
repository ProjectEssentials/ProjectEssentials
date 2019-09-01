@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.CommandsBase
import com.mairwunnx.projectessentials.proxy.ClientProxy
import com.mairwunnx.projectessentials.proxy.CommonProxy
import com.mairwunnx.projectessentials.proxy.ServerProxy
import com.mojang.brigadier.CommandDispatcher
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.command.CommandSource
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Supplier

const val MOD_ID = "projectessentials"
const val MOD_NAME = "Project Essentials"
const val MOD_VERSION = "0.0.1"

@Mod(MOD_ID)
object ProjectEssentials {
    private val logger: Logger = LogManager.getLogger()
    private val proxy: CommonProxy =
        DistExecutor.runForDist<CommonProxy>(
            { Supplier { ClientProxy() } },
            { Supplier { ServerProxy() } }
        )
    private lateinit var commandDispatcher: CommandDispatcher<CommandSource>

    init {
        logger.info("$MOD_NAME $MOD_VERSION starting initializing ...")
        FMLKotlinModLoadingContext.get().modEventBus.register(proxy)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME $MOD_VERSION starting setup mod ...")
        commandDispatcher = it.server.commandManager.dispatcher
        var isRemote = false
        logger.info("Getting isRemote state for worlds ...")
        it.server.worlds.forEach {
            logger.info(
                "    world: ${it.world.worldInfo.worldName}/${it.world.dimension.type.registryName} has isRemote state: ${it.isRemote}"
            )
            if (it.isRemote) isRemote = true
        }
        logger.debug("Creating CommandsBase instance with isRemote ($isRemote) value ...")
        val commandsBase = CommandsBase()
        logger.debug("Calling registerAll method from CommandsBase instance ...")
        commandsBase.registerAll(commandDispatcher)
    }
}
