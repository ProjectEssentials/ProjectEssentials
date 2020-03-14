package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.ProjectEssentials.Companion.afkPresenter
import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

// todo: make out of afk when player change position

object AfkCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = ModConfiguration.getCommandsConfig().commands.afk

    init {
        command = "afk"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = ModConfiguration.getCommandsConfig().commands.afk
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes {
                    return@executes execute(it)
                }
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
            if (PermissionsAPI.hasPermission(senderName, "ess.afk")) {
                if (afkPresenter.isInAfk(senderPlayer)) {
                    afkPresenter.removeAfkPlayer(senderPlayer)
                    senderPlayer.server.playerList.sendMessage(
                        TranslationTextComponent(
                            "project_essentials.afk_disabled", senderPlayer.name.string
                        ), false
                    )
                } else {
                    afkPresenter.setAfkPlayer(senderPlayer)
                    senderPlayer.server.playerList.sendMessage(
                        TranslationTextComponent(
                            "project_essentials.afk_enabled", senderPlayer.name.string
                        ), false
                    )
                }
            } else {
                throwPermissionLevel(senderName, command)
                sendMsg(sender, "afk.restricted", senderName)
                return 0
            }
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
