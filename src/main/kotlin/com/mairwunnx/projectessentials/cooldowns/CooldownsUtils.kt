package com.mairwunnx.projectessentials.cooldowns

import com.mairwunnx.projectessentials.commands.helpers.CommandAliases
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.extensions.empty
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.extensions.source
import com.mairwunnx.projectessentials.helpers.COOLDOWN_NOT_EXPIRED
import kotlinx.serialization.UnstableDefault
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

@UnstableDefault
fun processCooldownOfCommand(
    commandName: String,
    commandSenderNickName: String,
    commandEvent: CommandEvent
): Boolean {
    var originalCommand = String.empty
    val cooldownsConfig = ModConfiguration.getCooldownsConfig()
    val cooldownsMap = processCooldownsCommandsList(
        cooldownsConfig.commandCooldowns
    )
    val command = when {
        originalCommand != String.empty -> originalCommand
        else -> commandName
    }
    val commandCooldown: Int = cooldownsMap[commandName]
        ?: CommandAliases.searchForAliasesForCooldown(
            commandName, cooldownsMap
        ).let {
            originalCommand = it.b
            return@let it.a
        }
        ?: cooldownsMap[CooldownBase.DEFAULT_COOLDOWN_LITERAL]
        ?: CooldownBase.DEFAULT_COOLDOWN

    if (!CooldownBase.getCooldownIsExpired(commandSenderNickName, command, commandCooldown)) {
        logger.warn(
            COOLDOWN_NOT_EXPIRED
                .replace("%0", commandSenderNickName)
                .replace("%1", command)
        )
        sendMsg(
            commandEvent.source,
            "common.cooldown.error",
            commandCooldown.minus(
                CooldownBase.getCooldown(commandSenderNickName, command)
            ).toInt().toString()
        )
        return true
    } else {
        CooldownBase.addCooldown(commandSenderNickName, command)
        return false
    }
}
