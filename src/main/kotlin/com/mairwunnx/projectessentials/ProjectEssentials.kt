@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.*
import com.mairwunnx.projectessentials.commands.helpers.CommandAliases
import com.mairwunnx.projectessentials.commands.moderator.GetPosCommand
import com.mairwunnx.projectessentials.commands.teleport.*
import com.mairwunnx.projectessentials.commands.time.*
import com.mairwunnx.projectessentials.commands.weather.RainCommand
import com.mairwunnx.projectessentials.commands.weather.StormCommand
import com.mairwunnx.projectessentials.commands.weather.SunCommand
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.configurations.UserDataConfiguration
import com.mairwunnx.projectessentials.core.EssBase
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.commandName
import com.mairwunnx.projectessentials.core.extensions.player
import com.mairwunnx.projectessentials.core.helpers.DISABLED_COMMAND
import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.localization.processLocalizations
import com.mairwunnx.projectessentials.extensions.fullName
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mairwunnx.projectessentials.states.AfkPresenter
import com.mairwunnx.projectessentials.states.TeleportPresenter
import com.mairwunnx.projectessentials.storage.StorageBase
import com.mairwunnx.projectessentials.storage.UserData
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.entity.player.PlayerEvent.*
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import java.io.File

val USER_DATA_FOLDER = MOD_CONFIG_FOLDER + File.separator + "user-data"
val COOLDOWNS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "cooldowns.json"
val COMMANDS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "commands.json"

@Mod("project_essentials")
class ProjectEssentials : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modInstance = this
        afkPresenter = AfkPresenter()
        modVersion = "1.14.4-1.0.2"
        modModuleName = "Essentials"
        modSources = "https://github.com/ProjectEssentials/ProjectEssentials/"
        modCurseForge = "https://www.curseforge.com/minecraft/mc-mods/project-essentials/"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        ModConfiguration.loadConfig()
        StorageBase.loadUserData()
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onServerStarting(it: FMLServerStartingEvent) {
        teleportPresenter = TeleportPresenter(it.server)
        teleportPresenter.configureTimeOut()
        loadAdditionalModules()
        registerCommands(it.server.commandManager.dispatcher)
    }

    private fun registerCommands(
        cmdDispatcher: CommandDispatcher<CommandSource>
    ) {
        HelpCommand.register(cmdDispatcher)
        GetPosCommand.register(cmdDispatcher)
        SendPosCommand.register(cmdDispatcher)
        PingCommand.register(cmdDispatcher)
        HealCommand.register(cmdDispatcher)
        FeedCommand.register(cmdDispatcher)
        TopCommand.register(cmdDispatcher)
        AirCommand.register(cmdDispatcher)
        FlyCommand.register(cmdDispatcher)
        GodCommand.register(cmdDispatcher)
        BreakCommand.register(cmdDispatcher)
        MoreCommand.register(cmdDispatcher)
        DayCommand.register(cmdDispatcher)
        NightCommand.register(cmdDispatcher)
        MidnightCommand.register(cmdDispatcher)
        NoonCommand.register(cmdDispatcher)
        SunsetCommand.register(cmdDispatcher)
        SunriseCommand.register(cmdDispatcher)
        SuicideCommand.register(cmdDispatcher)
        RainCommand.register(cmdDispatcher)
        StormCommand.register(cmdDispatcher)
        SunCommand.register(cmdDispatcher)
        RepairCommand.register(cmdDispatcher)
        AfkCommand.register(cmdDispatcher)
        BurnCommand.register(cmdDispatcher)
        LightningCommand.register(cmdDispatcher)
        TpPosCommand.register(cmdDispatcher)
        TpAllCommand.register(cmdDispatcher)
        TpHereCommand.register(cmdDispatcher)
        TpaCommand.register(cmdDispatcher)
        TpAcceptCommand.register(cmdDispatcher)
        TpDenyCommand.register(cmdDispatcher)
        TpToggleCommand.register(cmdDispatcher)
        TpaCancelCommand.register(cmdDispatcher)
        TpaHereCommand.register(cmdDispatcher)
        TpaAllCommand.register(cmdDispatcher)
        EssentialsCommand.register(cmdDispatcher)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down $modName mod ...")
        ModConfiguration.saveConfig()
        StorageBase.saveUserData()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (it.parseResults.context.source.entity is ServerPlayerEntity) {
            val commandName = it.commandName
            val commandSender = it.player
            val commandSenderNickName = commandSender!!.name.string
            if (isBlockedCommand(it)) {
                logger.warn(
                    DISABLED_COMMAND
                        .replace("%0", commandSenderNickName)
                        .replace("%1", commandName)
                )
                sendMsg(commandSender.commandSource, "common.command.blocked")
                it.isCanceled = true
                return
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
    fun onPlayerJoin(event: PlayerLoggedInEvent) = processAbilities(event.player)

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerLoggedOutEvent) = savePlayerData(event.player)

    @SubscribeEvent
    fun onPlayerChangedDim(event: PlayerChangedDimensionEvent) = processAbilities(event.player)

    private val userDataConfiguration by lazy {
        getConfigurationByName<UserDataConfiguration>("user-data")
    }

    private fun processAbilities(player: PlayerEntity) {
        player as ServerPlayerEntity
        val config = ModConfiguration.getCommandsConfig().commands
        if (config.fly.autoFlyEnabled) {
            if (
                hasPermission(player, "ess.fly.auto", 3) ||
                hasPermission(player, "ess.fly.self", 2)
            ) {
                if (FlyCommand.validateWorld(player) && FlyCommand.validateMode(player)) {
                    val abilities = player.abilities
                    abilities.allowEdit = true

                    // @formatter:off
                    userDataConfiguration.take().users.find {
                        player.name.string == it.name || player.uniqueID.toString() == it.uuid
                    }?.let {
                        val result = player.currentDimensionName in it.flyWorlds && (FlyCommand.validateWorld(player) && FlyCommand.validateMode(player))
                        abilities.allowFlying = result
                        abilities.isFlying = result
                        player.sendPlayerAbilities()
                        if (result) {
                            MessagingAPI.sendMessage(
                                player, "${MESSAGE_MODULE_PREFIX}basic.fly.auto.success"
                            )
                        }
                    }
                    // @formatter:on
                }
            }
        }
        if (config.god.autoGodModeEnabled) {
            if (
                hasPermission(player, "ess.god.auto", 3) ||
                hasPermission(player, "ess.god.self", 2)
            ) {
                if (GodCommand.validateWorld(player) && GodCommand.validateMode(player)) {
                    val abilities = player.abilities
                    abilities.allowEdit = true

                    // @formatter:off
                    userDataConfiguration.take().users.find {
                        player.name.string == it.name || player.uniqueID.toString() == it.uuid
                    }?.let {
                        val result = player.currentDimensionName in it.godWorlds && (GodCommand.validateWorld(player) && GodCommand.validateMode(player))
                        abilities.disableDamage = result
                        player.sendPlayerAbilities()
                        if (result) {
                            MessagingAPI.sendMessage(
                                player, "${MESSAGE_MODULE_PREFIX}basic.god.auto.success"
                            )
                        }
                    }
                    // @formatter:on
                }
            }
        }
    }

    private fun savePlayerData(player: PlayerEntity) {
        val uuid = player.gameProfile.id
        val uuidString = uuid.toString()
        StorageBase.setData(
            uuidString, UserData(
                player.world.fullName(),
                "${player.posX.toInt()}, ${player.posY.toInt()}, ${player.posZ.toInt()}",
                getFlyEnabledWorlds(player, StorageBase.getData(uuidString).flyWorlds),
                getGodEnabledWorlds(player, StorageBase.getData(uuidString).godWorlds)
            )
        )
    }

    private fun getFlyEnabledWorlds(
        player: PlayerEntity,
        flyAbleWorlds: List<String>
    ): List<String> {
        if (getFly(player)) {
            if (!flyAbleWorlds.contains(player.world.worldInfo.worldName)) {
                val list = flyAbleWorlds.toMutableList()
                list.add(player.world.worldInfo.worldName)
                return list
            }
        } else {
            val list = flyAbleWorlds.toMutableList()
            list.remove(player.world.worldInfo.worldName)
            return list
        }
        return flyAbleWorlds
    }

    private fun getGodEnabledWorlds(
        player: PlayerEntity,
        godAbleWorlds: List<String>
    ): List<String> {
        if (getGod(player)) {
            if (!godAbleWorlds.contains(player.world.worldInfo.worldName)) {
                val list = godAbleWorlds.toMutableList()
                list.add(player.world.worldInfo.worldName)
                return list
            }
        } else {
            val list = godAbleWorlds.toMutableList()
            list.remove(player.world.worldInfo.worldName)
            return list
        }
        return godAbleWorlds
    }

    private fun getFly(player: PlayerEntity?): Boolean {
        if (player == null) return false
        return if (player.onGround) {
            player.abilities.allowFlying
        } else {
            player.abilities.isFlying || player.abilities.allowFlying
        }
    }

    private fun getGod(player: PlayerEntity?): Boolean {
        if (player == null) return false
        return player.abilities.disableDamage
    }

    companion object {
        lateinit var modInstance: ProjectEssentials
        lateinit var afkPresenter: AfkPresenter
        lateinit var teleportPresenter: TeleportPresenter
        var cooldownsInstalled: Boolean = false
    }
}
