@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.CommandsBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.cooldowns.CooldownBase
import com.mairwunnx.projectessentials.cooldowns.handleCooldown
import com.mairwunnx.projectessentials.extensions.commandName
import com.mairwunnx.projectessentials.extensions.player
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val MOD_ID = "project_essentials"
const val MOD_NAME = "Project Essentials"
const val MOD_VERSION = "1.14.4-0.0.3.0"

@Mod(MOD_ID)
class ProjectEssentials {
    private val logger: Logger = LogManager.getLogger()

    init {
        logger.info("$MOD_NAME $MOD_VERSION starting initializing ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading ProjectEssentials mod settings ...")
        ModConfiguration.loadConfig()
    }

    @SubscribeEvent
    fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME $MOD_VERSION starting mod loading ...")
        val commandsBase = CommandsBase()
        commandsBase.registerAll(it.server.commandManager.dispatcher)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down Project Essentials mod ...")
        logger.info("    - Saving configuration ...")
        ModConfiguration.saveConfig()
        logger.info("Done, thanks for using")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (it.parseResults.context.source.entity is ServerPlayerEntity) {
            val commandName = it.commandName
            val commandSender = it.player
            val commandSenderNickName = commandSender.name.string
            val cooldownsConfig = ModConfiguration.getCooldownsConfig()

            try {
                if (
                    !cooldownsConfig.ignoredPlayers.contains(commandSenderNickName) &&
                    !commandSender.hasPermissionLevel(cooldownsConfig.bypassPermissionLevel)
                ) {
                    it.isCanceled = handleCooldown(
                        commandName, commandSenderNickName, it
                    )
                }
            } catch (_: KotlinNullPointerException) {
                CooldownBase.addCooldown(
                    commandSenderNickName,
                    commandName
                )
            }
        }
    }
}
