package com.mairwunnx.projectessentials.commands.teleport

import com.mairwunnx.projectessentials.ProjectEssentials.Companion.teleportPresenter
import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

object TpToggleCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.tpToggle

    init {
        command = "tptoggle"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.tpToggle
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes { execute(it) }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)

        if (senderIsServer) {
            throwOnlyPlayerCan(command)
            return 0
        } else {
            if (PermissionsAPI.hasPermission(senderName, "ess.tptoggle")) {
                if (senderName in teleportPresenter.ignoredPlayers) {
                    teleportPresenter.ignoredPlayers.remove(senderName)
                    sendMsg(sender, "tptoggle.removed_successfully")
                    logger.info("Player $senderName removed from teleport ignore list")
                } else {
                    logger.info("player $senderName added to teleport ignore list")
                    teleportPresenter.ignoredPlayers.add(senderName)
                    sendMsg(sender, "tptoggle.added_successfully")
                }
            } else {
                throwPermissionLevel(senderName, command)
                sendMsg(sender, "tptoggle.restricted")
                return 0
            }
        }
        logger.info("Executed command \"/${command}\" from $senderName")
        return 0
    }
}
