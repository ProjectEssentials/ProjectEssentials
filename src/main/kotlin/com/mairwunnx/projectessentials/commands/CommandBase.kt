package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.empty
import com.mairwunnx.projectessentials.extensions.isPlayerSender
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.helpers.DISABLED_COMMAND_ARG
import com.mairwunnx.projectessentials.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentials.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.helpers.validateAlias
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.command.CommandSource
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import kotlin.properties.Delegates

@UnstableDefault
abstract class CommandBase<T : Any>(
    var commandInstance: T,
    private val aliasesIsExists: Boolean = true,
    private val hasArguments: Boolean = true,
    private val canServerExecuteWhenArgNone: Boolean = false,
    private val serverCanExecute: Boolean = true,
    val commandAliases: MutableList<String> = mutableListOf(),
    val commandArgName: String = "Player"
) {
    val config: CommandsConfig get() = getCommandsConfig()
    private var cmdAliases = mutableListOf<String>()
    private var cmdIsEnabledArgs = true
    private var cmdPermissionLevel by Delegates.notNull<Int>()
    private var cmdArgUsePermissionLevel by Delegates.notNull<Int>()

    lateinit var targetPlayer: ServerPlayerEntity
    var targetPlayerName = String.empty
    lateinit var sender: CommandSource
    lateinit var senderPlayer: ServerPlayerEntity
    var senderNickName = String.empty

    private val logger = LogManager.getLogger()
    val commandName by lazy {
        commandInstance.javaClass.simpleName.toLowerCase()
    }

    init {
        logger.info("    - register \"/$commandName\" command ...")
        assignCommandAliases()
        applyCommandAliases()
        assignCommandEnabledArgs()
        assignCommandPermissionLevel()
        assignCommandArgUsePermissionLevel()
        commandAliases.add(commandName)
    }

    open fun reload() {
        logger.info("    - reloading \"/$commandName\" command ...")
        assignCommandEnabledArgs()
        assignCommandPermissionLevel()
        assignCommandArgUsePermissionLevel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun assignCommandAliases() {
        if (aliasesIsExists) {
            cmdAliases = commandInstance.javaClass.getMethod(
                "getAliases"
            ).invoke(commandInstance) as MutableList<String>
            cmdAliases.forEach {
                logger.info("        - assigning \"/$commandName\" command alias: $it")
                when {
                    validateAlias(it) -> commandAliases.add(it)
                    else -> logger.error("        - alias assigning $it skipped: alias validation fail!")
                }
            }
        } else {
            logger.info("        - assigning \"/$commandName\" command aliases skipped")
        }
    }

    private fun applyCommandAliases() {
        logger.info("        - applying aliases: $commandAliases")
        CommandAliases.aliases[commandName] = commandAliases
    }

    private fun assignCommandEnabledArgs() {
        if (hasArguments) {
            cmdIsEnabledArgs = commandInstance.javaClass.getMethod(
                "getEnableArgs"
            ).invoke(commandInstance) as Boolean
        } else {
            logger.info("        - assigning \"/$commandName\" command enabled args param skipped")
        }
    }

    private fun assignCommandPermissionLevel() {
        cmdPermissionLevel = commandInstance.javaClass.getMethod(
            "getPermissionLevel"
        ).invoke(commandInstance) as Int
    }

    private fun assignCommandArgUsePermissionLevel() {
        if (hasArguments) {
            cmdArgUsePermissionLevel = commandInstance.javaClass.getMethod(
                "getArgUsePermissionLevel"
            ).invoke(commandInstance) as Int
        } else {
            logger.info("        - assigning \"/$commandName\" command arg use perm level param skipped")
        }
    }

    open fun register(dispatcher: CommandDispatcher<CommandSource>) = Unit

    /**
     * Return true if need to continue command executing;
     * else if need to cancel executing command.
     */
    protected open fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean = false
    ): Boolean {
        if (c.isPlayerSender()) {
            sender = c.source
            senderPlayer = sender.asPlayer()
            senderNickName = senderPlayer.name.string

            when {
                hasTarget && hasArguments && !cmdIsEnabledArgs -> {
                    logger.warn(
                        DISABLED_COMMAND_ARG
                            .replace("%0", senderNickName)
                            .replace("%1", commandName)
                    )
                    sendMsg(sender, "common.arg.error", commandName)
                    return false
                }
                hasTarget && hasArguments && cmdIsEnabledArgs -> assignTargets(c)
            }

            if (!hasTarget) {
                targetPlayer = c.source.asPlayer()
                targetPlayerName = targetPlayer.name.string
            }

            if (!senderPlayer.hasPermissionLevel(cmdPermissionLevel)) {
                logger.warn(
                    PERMISSION_LEVEL
                        .replace("%0", senderNickName)
                        .replace("%1", commandName)
                )
                when {
                    hasTarget -> sendMsg(sender, "$commandName.player.error", targetPlayerName)
                    else -> sendMsg(sender, "$commandName.self.error")
                }
                return false
            } else if (hasTarget && hasArguments && !senderPlayer.hasPermissionLevel(cmdArgUsePermissionLevel)) {
                sendMsg(sender, "$commandName.player.error", targetPlayerName)
                return false
            }
            return true
        } else {
            sender = c.source
            senderNickName = "server"
            return if (hasTarget && hasArguments && serverCanExecute) {
                assignTargets(c)
                true
            } else {
                if (!hasArguments && canServerExecuteWhenArgNone) return true
                logger.warn(ONLY_PLAYER_CAN.replace("%0", commandName))
                false
            }
        }
    }

    private fun assignTargets(c: CommandContext<CommandSource>) {
        targetPlayer = EntityArgument.getPlayer(
            c, commandArgName
        )
        targetPlayerName = targetPlayer.name.string
    }
}
