@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.CommandAliases
import com.mairwunnx.projectessentials.commands.CommandsBase
import com.mairwunnx.projectessentials.commands.FlyCommand
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.cooldowns.CooldownBase
import com.mairwunnx.projectessentials.cooldowns.processCooldownOfCommand
import com.mairwunnx.projectessentials.extensions.commandName
import com.mairwunnx.projectessentials.extensions.fullName
import com.mairwunnx.projectessentials.extensions.player
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mairwunnx.projectessentials.storage.UserData
import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val MOD_ID = "project_essentials"
const val MOD_NAME = "Project Essentials"
const val MOD_VERSION = "1.14.4-0.0.5.0"
const val MOD_DESCRIPTION = "minecraft command mod - adds commands for use in-game"
const val MOD_MAINTAINER = "MairwunNx (Pavel Erokhin)"
const val MOD_TARGET_FORGE = "28.0.X"
const val MOD_TARGET_MC = "1.14.4"
const val MOD_SOURCES_LINK = "https://github.com/MairwunNx/ProjectEssentials/"
const val MOD_TELEGRAM_LINK = "https://t.me/minecraftforge"

lateinit var commandsBase: CommandsBase
lateinit var commandDispatcher: CommandDispatcher<CommandSource>

@UnstableDefault
@Mod(MOD_ID)
class ProjectEssentials {
    private val logger: Logger = LogManager.getLogger()

    init {
        logger.info("$MOD_NAME $MOD_VERSION starting initializing ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading ProjectEssentials mod settings ...")
        ModConfiguration.loadConfig()
        StorageBase.loadUserData()
    }

    @SubscribeEvent
    fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME $MOD_VERSION starting mod loading ...")
        commandsBase = CommandsBase()
        commandDispatcher = it.server.commandManager.dispatcher
        commandsBase.registerAll(it.server.commandManager.dispatcher)
    }

    @UnstableDefault
    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down Project Essentials mod ...")
        logger.info("    - Saving configuration ...")
        ModConfiguration.saveConfig()
        logger.info("    - Saving user data ...")
        StorageBase.saveUserData()
        logger.info("Done, thanks for using")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (it.parseResults.context.source.entity is ServerPlayerEntity) {
            val commandName = it.commandName
            val commandSender = it.player
            val commandSenderNickName = commandSender.name.string
            val cooldownsConfig = ModConfiguration.getCooldownsConfig()

            if (isBlockedCommand(it)) {
                logger.warn(
                    DISABLED_COMMAND
                        .replace("%0", commandSenderNickName)
                        .replace("%1", commandName)
                )
                it.isCanceled = true
            }

            try {
                if (
                    !cooldownsConfig.ignoredPlayers.contains(commandSenderNickName) &&
                    !commandSender.hasPermissionLevel(cooldownsConfig.bypassPermissionLevel)
                ) {
                    it.isCanceled = processCooldownOfCommand(
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

    private fun isBlockedCommand(event: CommandEvent): Boolean {
        val commandName = event.commandName
        val commandConfig = ModConfiguration.getCommandsConfig()
        return when {
            commandConfig.disabledCommands.contains(commandName) -> true
            else -> CommandAliases.searchForAliases(commandName)
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerLoggedInEvent) {
        val config = ModConfiguration.getCommandsConfig().commands
        if (config.fly.autoFlyEnabled) {
            StorageBase.getData(event.player.uniqueID.toString()).worlds.forEach {
                if (it.worldName == event.player.world.fullName()) {
                    if (it.flyModeEnabled) {
                        if (event.player.commandSource.asPlayer().hasPermissionLevel(
                                config.fly.permissionLevel
                            )
                        ) {
                            if (FlyCommand.setFly(event.player.commandSource.asPlayer(), true)) {
                                sendMsg(event.player.commandSource, "fly.auto.success")
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        saveOrUpdatePlayerData(event.player)
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerLoggedOutEvent) {
        saveOrUpdatePlayerData(event.player)
    }

    private fun saveOrUpdatePlayerData(player: PlayerEntity) {
        val uuid = player.gameProfile.id
        val uuidString = uuid.toString()

        val lastWorld = player.world.fullName()
        val lastWorldPos = "${player.posX.toInt()}, ${player.posY.toInt()}, ${player.posZ.toInt()}"
        val oldPlayerData = StorageBase.getData(uuidString).worlds
        val worldList = mutableListOf<UserData.World>()
        player.world.server?.worlds?.forEach {
            worldList.add(
                UserData.World(
                    it.fullName(),
                    flyModeEnabled = getFly(it.getPlayerByUuid(uuid))
                )
            )
        }

        if (oldPlayerData.isEmpty()) {
            val list = mutableListOf(*oldPlayerData.toTypedArray())
            worldList.forEach { list.add(it) }
            StorageBase.getData(uuidString).worlds = list
            val data = UserData(lastWorld, lastWorldPos, list)
            StorageBase.setData(uuidString, data)
            return
        }

        if (oldPlayerData.isNotEmpty()) {
            worldList.forEach {
                val result = it.containsIn(oldPlayerData)
                if (result.b) {
                    val list = mutableListOf<UserData.World>()
                    oldPlayerData.forEach { world -> list.add(world) }
                    list[result.a] = it
                    StorageBase.getData(uuidString).worlds = list
                    val data = UserData(lastWorld, lastWorldPos, list)
                    StorageBase.setData(uuidString, data)
                } else if (!result.b) {
                    val list = mutableListOf<UserData.World>()
                    oldPlayerData.forEach { world -> list.add(world) }
                    list.add(it)
                    StorageBase.getData(uuidString).worlds = list
                    val data = UserData(lastWorld, lastWorldPos, list)
                    StorageBase.setData(uuidString, data)
                }
            }
        }
    }

    private fun getFly(player: PlayerEntity?): Boolean {
        if (player == null) return false
        return if (player.onGround) {
            player.abilities.allowFlying
        } else {
            player.abilities.isFlying
        }
    }
}
