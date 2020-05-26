package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.DamageSource

object SuicideCommand : CommandBase(suicideLiteral) {
    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    override val name = "suicide"

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.suicide", 2) { isServer ->
            if (isServer) {
                ServerMessagingAPI.throwOnlyPlayerCan()
            } else {
                context.getPlayer()!!.attackEntityFrom(
                    DamageSource(
                        if (generalConfiguration.getBool(SETTING_LOC_ENABLED)) "suicide" else "magic"
                    ).setDamageBypassesArmor().setDamageAllowedInCreativeMode(), Float.MAX_VALUE
                ).also { super.process(context) }
            }
        }
    }
}
