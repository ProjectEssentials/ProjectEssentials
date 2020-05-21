package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommandsConfig(
    @SerialName("Commands")
    val commands: Commands = Commands(),
    @SerialName("DisabledCommands")
    val disabledCommands: List<String> = listOf()
) {
    @Serializable
    data class Commands(
        @SerialName("Heal")
        val heal: Heal = Heal(),
        @SerialName("Feed")
        val feed: Feed = Feed(),
        @SerialName("Top")
        val top: Top = Top(),
        @SerialName("Air")
        val air: Air = Air(),
        @SerialName("Fly")
        val fly: Fly = Fly(),
        @SerialName("God")
        val god: God = God(),
        @SerialName("SendPos")
        val sendPos: SendPos = SendPos(),
        @SerialName("More")
        val more: More = More(),
        @SerialName("Suicide")
        val suicide: Suicide = Suicide(),
        @SerialName("Repair")
        val repair: Repair = Repair(),
        @SerialName("Ping")
        val ping: Ping = Ping(),
        @SerialName("Afk")
        val afk: Afk = Afk(),
        @SerialName("Burn")
        val burn: Burn = Burn(),
        @SerialName("Lightning")
        val lightning: Lightning = Lightning(),
        @SerialName("TpPos")
        val tpPos: TpPos = TpPos(),
        @SerialName("TpAll")
        val tpAll: TpAll = TpAll(),
        @SerialName("TpHere")
        val tpHere: TpHere = TpHere(),
        @SerialName("Tpa")
        val tpa: Tpa = Tpa(),
        @SerialName("TpaAll")
        val tpaAll: TpaAll = TpaAll(),
        @SerialName("TpaHere")
        val tpaHere: TpaHere = TpaHere(),
        @SerialName("TpAccept")
        val tpAccept: TpAccept = TpAccept(),
        @SerialName("TpDeny")
        val tpDeny: TpDeny = TpDeny(),
        @SerialName("TpToggle")
        val tpToggle: TpToggle = TpToggle(),
        @SerialName("TpaCancel")
        val tpaCancel: TpaCancel = TpaCancel(),
        @SerialName("Help")
        val help: Help = Help()
    ) {
        @Serializable
        data class Heal(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: List<String> = listOf("eheal")
        )

        @Serializable
        data class Feed(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("MaxFoodSaturationLevel")
            val maxFoodSaturationLevel: Float = 5.0f,
            @SerialName("MaxFoodLevel")
            val maxFoodLevel: Int = 20,
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "eat", "eeat", "efeed"
            )
        )

        @Serializable
        data class Top(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etop")
        )

        @Serializable
        data class Air(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: List<String> = listOf("eair")
        )

        @Serializable
        data class Fly(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: List<String> = listOf("efly"),
            @SerialName("AutoFlyEnabled")
            val autoFlyEnabled: Boolean = true,
            @SerialName("FlyDisabledWorlds")
            val flyDisabledWorlds: List<String> = listOf()
        )

        @Serializable
        data class God(
            @SerialName("EnableArgs")
            val enableArgs: Boolean = true,
            @SerialName("Aliases")
            val aliases: List<String> = listOf("egod", "tgm"),
            @SerialName("AutoGodModeEnabled")
            val autoGodModeEnabled: Boolean = true,
            @SerialName("GodModeDisabledWorlds")
            val godModeDisabledWorlds: List<String> = listOf()
        )

        @Serializable
        data class SendPos(
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "esendpos"
            )
        )

        @Serializable
        data class More(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("emore", "dupe")
        )

        @Serializable
        data class Suicide(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("esuicide")
        )

        @Serializable
        data class Repair(
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "fix", "efix", "erepair"
            )
        )

        @Serializable
        data class Ping(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("eping")
        )

        @Serializable
        data class Afk(
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "afk", "eafk", "away", "eaway"
            )
        )

        @Serializable
        data class Burn(
            @SerialName("DefaultDuration")
            val defaultDuration: Int = 10,
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "burn", "eburn"
            )
        )

        @Serializable
        data class Lightning(
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "lightning", "elightning", "shock", "eshock", "thor", "ethor"
            )
        )

        @Serializable
        data class TpPos(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etppos")
        )

        @Serializable
        data class TpAll(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpall")
        )

        @Serializable
        data class TpHere(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etphere", "s")
        )

        @Serializable
        data class Tpa(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpa", "call", "ecall"),
            @SerialName("TimeOut")
            val timeOut: Int = 45
        )

        @Serializable
        data class TpaAll(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpaall", "callall", "ecallall")
        )

        @Serializable
        data class TpaHere(
            @SerialName("Aliases")
            val aliases: List<String> = listOf(
                "etpahere",
                "callhere",
                "ecallhere"
            )
        )

        @Serializable
        data class TpAccept(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpaccept", "tpyes", "etpyes")
        )

        @Serializable
        data class TpDeny(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpdeny", "tpno", "etpno")
        )

        @Serializable
        data class TpToggle(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etptoggle", "tpoff", "etpoff")
        )

        @Serializable
        data class TpaCancel(
            @SerialName("Aliases")
            val aliases: List<String> = listOf("etpacancel")
        )

        @Serializable
        data class Help(
            @SerialName("Help")
            val aliases: List<String> = listOf("ehelp"),
            @SerialName("ReplaceDefault")
            val replaceDefault: Boolean = true,
            @SerialName("MaxLines")
            val maxLines: Int = 8,
            @SerialName("TryColorize")
            val tryColorize: Boolean = true,
            @SerialName("CommandColor")
            val commandColor: String = "§7",
            @SerialName("BracketColor")
            val bracketColor: String = "§8",
            @SerialName("OrOperatorColor")
            val orOperatorColor: String = "§8",
            @SerialName("PlainTextColor")
            val plainTextColor: String = "§7",
            @SerialName("MandatoryColor")
            val mandatoryColor: String = "§c",
            @SerialName("RedirectColor")
            val redirectColor: String = "§d"
        )
    }
}
