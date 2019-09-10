package com.mairwunnx.projectessentials.cooldowns

import com.mairwunnx.projectessentials.commands.COOLDOWN_NOT_EXPIRED
import com.mairwunnx.projectessentials.commands.CommandAliases
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.source
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.event.CommandEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger = LogManager.getLogger()

fun processCooldownsCommandsList(
    cooldowns: List<String>
): HashMap<String, Int> {
    val hashMap = hashMapOf<String, Int>()
    cooldowns.forEach {
        val command = it.split("=")[0].toLowerCase()
        val cooldown = it.split("=")[1].toInt()
        hashMap[command] = cooldown
    }
    return hashMap
}

fun handleCooldown(
    commandName: String,
    commandSenderNickName: String,
    commandEvent: CommandEvent
): Boolean {
    var originalCommand = ""
    val cooldownsConfig = ModConfiguration.getCooldownsConfig()
    val cooldownsMap = processCooldownsCommandsList(
        cooldownsConfig.commandCooldowns
    )
    val commandCooldown: Int = cooldownsMap[commandName]
        ?: CommandAliases.searchForAliases(
            commandName, cooldownsMap
        ).let {
            originalCommand = it.second
            return@let it.first
        }
        ?: cooldownsMap[CooldownBase.DEFAULT_COOLDOWN_LITERAL]
        ?: CooldownBase.DEFAULT_COOLDOWN

    val command = if (originalCommand != "") {
        originalCommand
    } else {
        commandName
    }

    if (!CooldownBase.getCooldownIsExpired(
            commandSenderNickName,
            command,
            commandCooldown
        )
    ) {
        logger.warn(
            COOLDOWN_NOT_EXPIRED
                .replace("%0", commandSenderNickName)
                .replace("%1", command)
        )

        commandEvent.source.sendFeedback(
            TranslationTextComponent(
                "project_essentials.common.cooldown.error",
                commandCooldown.minus(
                    CooldownBase.getCooldown(commandSenderNickName, command)
                ).toInt()
            ), false
        )
        return true
    } else {
        CooldownBase.addCooldown(commandSenderNickName, command)
        return false
    }
}
