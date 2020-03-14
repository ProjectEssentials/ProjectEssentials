package com.mairwunnx.projectessentials.extensions

import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.localization.sendMsgV2
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent

fun sendMsg(
    commandSource: CommandSource,
    l10nString: String,
    vararg args: String
) {
    if (LocalizationConfigurationUtils.getConfig().enabled) {
        sendMsgV2(
            commandSource.asPlayer(),
            "project_essentials.$l10nString", *args
        )
    } else {
        commandSource.sendFeedback(
            TranslationTextComponent(
                "project_essentials.$l10nString", *args
            ), false
        )
    }
}
