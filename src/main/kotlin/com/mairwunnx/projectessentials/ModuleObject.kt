@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("project_essentials_basic")
class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split("\\s+").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 2

    private val logger = LogManager.getLogger()

    init {
        subscribeEvents()
        EVENT_BUS.register(this)
    }

    override fun init() = Unit

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
