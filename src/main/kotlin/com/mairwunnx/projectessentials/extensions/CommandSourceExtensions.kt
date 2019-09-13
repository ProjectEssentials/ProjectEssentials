package com.mairwunnx.projectessentials.extensions

import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent

fun sendMsg(
    commandSource: CommandSource,
    l10nString: String,
    vararg args: String
) {
    commandSource.sendFeedback(
        TranslationTextComponent(
            "project_essentials.$l10nString", *args
        ), false
    )
}
