package com.mairwunnx.projectessentials.extensions

import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.localization.sendMsgV2
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent

fun sendMsg(
    commandSource: CommandSource,
    l10nString: String,
    vararg args: Any
) {
    if (LocalizationConfigurationUtils.getConfig().enabled) {
        @Suppress("UNCHECKED_CAST")
        sendMsgV2(
            commandSource.asPlayer(),
            "project_essentials.$l10nString", *args as Array<out String>
        )
    } else {
        commandSource.sendFeedback(
            TranslationTextComponent(
                "project_essentials.$l10nString", *args
            ), false
        )
    }
}
