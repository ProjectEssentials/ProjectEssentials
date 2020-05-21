package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.api.commands.CommandsAPI
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.extensions.playerName
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.CommandSource
import net.minecraft.util.text.StringTextComponent
import org.apache.logging.log4j.LogManager

object HelpCommand : CommandsAPI(
    "help",
    getCommandsConfig().commands.help.aliases,
    helpLiteral,
    getCommandsConfig().commands.help.replaceDefault
) {
    private val logger = LogManager.getLogger()

    init {
        onCommandExecute(HelpCommand::execute)
    }

    fun execute(
        context: CommandContext<CommandSource>
    ): Int {
        when {
            !context.isPlayerSender() || PermissionsAPI.hasPermission(
                context.playerName(), "ess.help"
            ) || PermissionsAPI.hasPermission(
                context.playerName(), "native.help"
            ) -> {
                val dispatcher = getDispatcher()
                val linesPerPage = getCommandsConfig().commands.help.maxLines
                val map: Map<CommandNode<CommandSource>, String> =
                    dispatcher.getSmartUsage(dispatcher.root, context.source)

                val pages = map.values.count() / linesPerPage + 1
                val page = if (getIntExisting(context, "page")) {
                    getInt(context, "page")
                } else {
                    1
                }

                val displayedLines = page * linesPerPage
                val droppedLines = displayedLines - linesPerPage
                val values = map.values.take(displayedLines).drop(droppedLines)

                if (context.isPlayerSender()) {
                    sendMsg(
                        context.source,
                        "help.pages_out",
                        page.toString(), pages.toString()
                    )
                } else {
                    logger.info("Help page $page of $pages")
                }

                values.forEach {
                    context.source.sendFeedback(
                        StringTextComponent(
                            colorize(
                                "/$it"
                            )
                        ), false
                    )
                }
            }
            else -> {
                sendMsg(context.source, "help.restricted")
            }
        }
        return 0
    }

    private fun colorize(input: String): String {
        if (!getCommandsConfig().commands.help.tryColorize) return input
        val help = getCommandsConfig().commands.help

        return input
            .replace(
                "/", "${help.commandColor}/"
            ).replace(
                " ", "${help.plainTextColor} "
            ).replace(
                "(", "${help.bracketColor}(${help.mandatoryColor}"
            ).replace(
                "|", "${help.orOperatorColor}|${help.mandatoryColor}"
            ).replace(
                ")", "${help.bracketColor})${help.plainTextColor}"
            ).replace(
                "[", "${help.bracketColor}[${help.mandatoryColor}"
            ).replace(
                "]", "${help.bracketColor}]${help.plainTextColor}"
            ).replace(
                "<", "${help.bracketColor}<${help.mandatoryColor}"
            ).replace(
                ">", "${help.bracketColor}>${help.plainTextColor}"
            ).replace(
                "-${help.bracketColor}>${help.plainTextColor}",
                "${help.redirectColor}->${help.plainTextColor}"
            )
    }
}
