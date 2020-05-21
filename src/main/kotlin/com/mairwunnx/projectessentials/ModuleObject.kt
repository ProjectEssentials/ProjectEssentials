@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
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

    private val logger = LogManager.getLogger()

    init {
        subscribeEvents()
        EVENT_BUS.register(this)
    }

    override fun init() = initializeModuleSettings()

    private fun initializeModuleSettings() {
        generalConfiguration.getFloatOrDefault(SETTING_MAX_FOOD_SATURATION_LEVEL, 5.0f)
        generalConfiguration.getIntOrDefault(SETTING_MAX_FOOD_LEVEL, 20)
        generalConfiguration.getBoolOrDefault(SETTING_AUTO_FLY_MODE_ON_JOIN_ENABLED, true)
        generalConfiguration.getBoolOrDefault(SETTING_AUTO_GOD_MODE_ON_JOIN_ENABLED, true)
        generalConfiguration.getIntOrDefault(SETTING_BURN_COMMAND_DURATION, 10)
        generalConfiguration.getIntOrDefault(SETTING_TELEPORT_REQUEST_TIMEOUT, 45)
        generalConfiguration.getBoolOrDefault(SETTING_REPLACE_NATIVE_HELP_COMMAND, true)
        generalConfiguration.getIntOrDefault(SETTING_HELP_COMMAND_MAX_LINES_OUT, 8)
        generalConfiguration.getBoolOrDefault(SETTING_HELP_COMMAND_COLORIZED_OUT, true)
        generalConfiguration.getList(SETTING_FIRST_JOIN_COMMANDS, arrayListOf())
        generalConfiguration.getIntOrDefault(SETTING_AFK_IDLENESS_TIME, 300)
        generalConfiguration.getBoolOrDefault(SETTING_AFK_HANDLE_ACTIVITY, true)
        generalConfiguration.getList(SETTING_DISABLED_COMMANDS, arrayListOf())

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
}
