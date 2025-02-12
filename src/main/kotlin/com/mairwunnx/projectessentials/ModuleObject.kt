@file:Suppress("unused")

package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.FlyCommand
import com.mairwunnx.projectessentials.commands.GodCommand
import com.mairwunnx.projectessentials.configurations.UserDataConfigurationModel
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.extensions.commandName
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.extensions.directoryName
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.managers.AfkManager
import com.mairwunnx.projectessentials.managers.UserManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import java.time.ZonedDateTime

@Mod("project_essentials_basic")
class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split(" ").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 2

    private val logger = LogManager.getLogger()

    init {
        providers.forEach(ProviderAPI::addProvider)
        initLocalization()
        EVENT_BUS.register(this)
    }

    private fun initLocalization() {
        LocalizationAPI.apply(this.javaClass) {
            mutableListOf(
                "/assets/projectessentials/lang/en_us.json",
                "/assets/projectessentials/lang/ru_ru.json",
                "/assets/projectessentials/lang/zh_cn.json"
            )
        }
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
        generalConfiguration.getIntOrDefault(SETTING_AFK_IDLENESS_KICK_TIME, Int.MAX_VALUE)
        generalConfiguration.getBoolOrDefault(SETTING_AFK_HANDLE_ACTIVITY, true)
        generalConfiguration.getList(SETTING_DISABLED_COMMANDS, arrayListOf())
        generalConfiguration.getList(SETTING_FLY_WORLDS_DISABLED, arrayListOf())
        generalConfiguration.getList(SETTING_GOD_WORLDS_DISABLED, arrayListOf())
        generalConfiguration.getBoolOrDefault(SETTING_INVSEE_DISABLE_DANGER_SLOTS, true)

        ConfigureEssentialsCommandAPI.required(SETTING_REPLACE_NATIVE_HELP_COMMAND)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(it: CommandEvent) {
        if (!handleBlockedCommand(it)) it.isCanceled = true
    }

    @SubscribeEvent
    fun onPlayerUpdate(event: TickEvent.PlayerTickEvent) {
        withServerPlayer(event.player) { AfkManager.handle(it) }
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        withServerPlayer(event.player) { disposeAfkStates(it).run { savePlayerData(it) } }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        withServerPlayer(event.player) {
            processPlayerAbilities(it).run { executeFirstLoginCommands(it) }
        }
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        withServerPlayer(event.player) { savePlayerData(it).run { processPlayerAbilities(it) } }
    }

    internal fun savePlayerData(player: ServerPlayerEntity) {
        val uuid = player.uniqueID.toString()
        val name = player.name.string
        UserManager.getUserByNameOrUUID(player.name.string, player.uniqueID.toString())?.also {
            it.isInvisible = player.isInvisible
            it.lastDateTime = ZonedDateTime.now().toString()
            it.lastWorldName = player.serverWorld.directoryName
            it.lastDimension = player.currentDimensionName
            it.lastPosition = player.position.toString()
            it.lastIPAddress = player.playerIP
            it.flyWorldDimensions = getFlyEnabledWorlds(player, it.flyWorldDimensions)
            it.godWorldDimensions = getGodEnabledWorlds(player, it.godWorldDimensions)
        } ?: run {
            userDataConfiguration.users.add(
                UserDataConfigurationModel.User(
                    name, uuid,
                    player.isInvisible,
                    ZonedDateTime.now().toString(),
                    player.serverWorld.directoryName,
                    player.currentDimensionName,
                    player.position.toString(),
                    player.playerIP,
                    mutableListOf(),
                    getFlyEnabledWorlds(player, mutableListOf()),
                    getGodEnabledWorlds(player, mutableListOf())
                )
            )
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

    private fun processPlayerAbilities(player: ServerPlayerEntity) {
        if (hasPermission(player, "ess.vanish.auto", 4)) {
            UserManager.getUserByNameOrUUID(player.name.string, player.uniqueID.toString())?.let {
                player.isInvisible = it.isInvisible
            }
        }

        if (generalConfiguration.getBool(SETTING_AUTO_FLY_MODE_ON_JOIN_ENABLED)) {
            processPlayerNamedAbility(player, "fly") {
                with(player.abilities) {
                    allowEdit = true
                    UserManager.getUserByNameOrUUID(
                        player.name.string, player.uniqueID.toString()
                    )?.let {
                        val inFlyWorld = it.flyWorldDimensions.contains(
                            "${player.serverWorld.directoryName}&${player.currentDimensionName}"
                        )
                        allowFlying = inFlyWorld
                        isFlying = inFlyWorld
                        if (inFlyWorld) MessagingAPI.sendMessage(
                            player, "${MESSAGE_MODULE_PREFIX}basic.fly.auto.success"
                        )
                        return@processPlayerNamedAbility
                    }
                    allowFlying = false
                    isFlying = false
                }
            }
        }

        if (generalConfiguration.getBool(SETTING_AUTO_GOD_MODE_ON_JOIN_ENABLED)) {
            processPlayerNamedAbility(player, "god") {
                with(player.abilities) {
                    allowEdit = true
                    UserManager.getUserByNameOrUUID(
                        player.name.string, player.uniqueID.toString()
                    )?.let {
                        val inGodWorld = it.godWorldDimensions.contains(
                            "${player.serverWorld.directoryName}&${player.currentDimensionName}"
                        )
                        disableDamage = inGodWorld
                        if (inGodWorld) MessagingAPI.sendMessage(
                            player, "${MESSAGE_MODULE_PREFIX}basic.god.auto.success"
                        )
                        return@processPlayerNamedAbility
                    }
                    disableDamage = false
                }
            }
        }

        player.sendPlayerAbilities()
    }

    private fun processPlayerNamedAbility(
        player: ServerPlayerEntity, ability: String, action: () -> Unit
    ) {
        if (
            hasPermission(player, "ess.$ability.auto", 3) ||
            hasPermission(player, "ess.$ability.self", 2)
        ) when (ability) {
            "fly" -> if (FlyCommand.validateWorld(player) && FlyCommand.validateMode(player)) action()
            "god" -> if (GodCommand.validateWorld(player) && GodCommand.validateMode(player)) action()
        }
    }

    private fun executeFirstLoginCommands(player: ServerPlayerEntity) {
        UserManager.getUserByNameOrUUID(player.name.string, player.uniqueID.toString()) ?: run {
            generalConfiguration.getList(SETTING_FIRST_JOIN_COMMANDS).forEach {
                player.server.commandManager.handleCommand(player.commandSource, it)
            }
        }
    }

    private fun getFlyEnabledWorlds(
        player: ServerPlayerEntity, flyAbleWorlds: MutableList<String>
    ): MutableList<String> {
        fun isFly() =
            if (player.onGround) player.abilities.allowFlying else player.abilities.isFlying || player.abilities.allowFlying

        if (isFly()) {
            if ("${player.serverWorld.directoryName}&${player.currentDimensionName}" !in flyAbleWorlds) {
                flyAbleWorlds.add("${player.serverWorld.directoryName}&${player.currentDimensionName}")
            }
        } else {
            flyAbleWorlds.remove("${player.serverWorld.directoryName}&${player.currentDimensionName}")
        }
        return flyAbleWorlds
    }

    private fun getGodEnabledWorlds(
        player: ServerPlayerEntity, godAbleWorlds: MutableList<String>
    ): MutableList<String> {
        if (player.abilities.disableDamage) {
            if ("${player.serverWorld.directoryName}&${player.currentDimensionName}" !in godAbleWorlds) {
                godAbleWorlds.add("${player.serverWorld.directoryName}&${player.currentDimensionName}")
            }
        } else {
            godAbleWorlds.remove("${player.serverWorld.directoryName}&${player.currentDimensionName}")
        }
        return godAbleWorlds
    }

    private inline fun withServerPlayer(
        player: PlayerEntity?, spe: (ServerPlayerEntity) -> Unit
    ) {
        if (player is ServerPlayerEntity) spe(player)
    }
}
