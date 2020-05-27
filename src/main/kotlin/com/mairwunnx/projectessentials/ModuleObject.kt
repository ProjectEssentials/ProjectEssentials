@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.configurations.UserDataConfiguration
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.extensions.commandName
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.managers.AfkManager
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("project_essentials_basic")
class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split("\\s+").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 2

    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    private val userDataConfiguration by lazy {
        getConfigurationByName<UserDataConfiguration>("user-data")
    }

    private val logger = LogManager.getLogger()

    init {
        providers.forEach(ProviderAPI::addProvider)
        subscribeEvents()
        EVENT_BUS.register(this)
    }

    override fun init() = initializeModuleSettings()

    private fun initializeModuleSettings() {
        generalConfiguration.getFloatOrDefault(SETTING_MAX_FOOD_SATURATION_LEVEL, 5.0f)
        generalConfiguration.getIntOrDefault(SETTING_MAX_FOOD_LEVEL, 20)
        generalConfiguration.getBoolOrDefault(SETTING_AUTO_FLY_MODE_ON_JOIN_ENABLED, true)
        generalConfiguration.getBoolOrDefault(SETTING_AUTO_GOD_MODE_ON_JOIN_ENABLED, true)
        generalConfiguration.getIntOrDefault(SETTING_TELEPORT_REQUEST_TIMEOUT, 45)
        generalConfiguration.getBoolOrDefault(SETTING_REPLACE_NATIVE_HELP_COMMAND, true)
        generalConfiguration.getIntOrDefault(SETTING_HELP_COMMAND_MAX_LINES_OUT, 8)
        generalConfiguration.getBoolOrDefault(SETTING_HELP_COMMAND_COLORIZED_OUT, true)
        generalConfiguration.getList(SETTING_FIRST_JOIN_COMMANDS, arrayListOf())
        generalConfiguration.getIntOrDefault(SETTING_AFK_IDLENESS_TIME, 300)
        generalConfiguration.getBoolOrDefault(SETTING_AFK_HANDLE_ACTIVITY, true)
        generalConfiguration.getList(SETTING_DISABLED_COMMANDS, arrayListOf())
        generalConfiguration.getList(SETTING_FLY_WORLDS_DISABLED, arrayListOf())
        generalConfiguration.getList(SETTING_GOD_WORLDS_DISABLED, arrayListOf())

        ConfigureEssentialsCommandAPI.required(SETTING_REPLACE_NATIVE_HELP_COMMAND)
    }

    private fun subscribeEvents() {
        ModuleEventAPI.subscribeOn<FMLCommonSetupEventData>(
            ForgeEventType.SetupEvent
        ) {
            LocalizationAPI.apply(
                Localization(
                    mutableListOf(
                        "/assets/projectessentials/lang/en_us.json",
                        "/assets/projectessentials/lang/ru_ru.json",
                        "/assets/projectessentials/lang/zh_cn.json"
                    ), "basic", ModuleObject::class.java
                )
            )
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (!handleBlockedCommand(it)) it.isCanceled = true
    }

    @SubscribeEvent
    fun onPlayerUpdate(event: TickEvent.PlayerTickEvent) {
        if (event.player is ServerPlayerEntity) {
            AfkManager.handle(event.player as ServerPlayerEntity)
        }
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (event.player is ServerPlayerEntity) {
            val player = event.player as ServerPlayerEntity
            disposeAfkStates(player)
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.player is ServerPlayerEntity) {
            val player = event.player as ServerPlayerEntity
            executeFirstLoginCommands(player)
        }
    }

    private fun handleBlockedCommand(it: CommandEvent): Boolean {
        if (generalConfiguration.getList(SETTING_DISABLED_COMMANDS).isNotEmpty()) {
            val player = it.parseResults.context.source.entity
            if (player is ServerPlayerEntity) {
                if (
                    it.commandName in generalConfiguration.getList(SETTING_DISABLED_COMMANDS) ||
                    isAliasesOfBlockedCommand(it.commandName)
                ) MessagingAPI.sendMessage(
                    player, "${MESSAGE_MODULE_PREFIX}basic.command.blocked"
                ).run { return false }
            }
        }
        return true
    }

    private fun disposeAfkStates(player: ServerPlayerEntity) {
        AfkManager.getAfkPlayers().remove(player)
    }

    private fun executeFirstLoginCommands(player: ServerPlayerEntity) {
        userDataConfiguration.take().users.find {
            it.name == player.name.string || it.uuid == player.uniqueID.toString()
        } ?: run {
            generalConfiguration.getList(SETTING_FIRST_JOIN_COMMANDS).forEach {
                player.server.commandManager.handleCommand(player.commandSource, it)
            }
        }
    }
}
