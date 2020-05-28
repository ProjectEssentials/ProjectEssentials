package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.arguments.StringArrayArgument
import com.mairwunnx.projectessentials.managers.KitManager
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument

val afkLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("afk").then(
        Commands.literal("list").then(
            Commands.argument(
                "page", IntegerArgumentType.integer()
            ).executes { AfkCommand.afkList(it) }
        ).executes { AfkCommand.afkList(it) }
    ).executes { AfkCommand.afkSet(it) }

val airLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("air").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { AirCommand.airOther(it) }
    ).executes { AirCommand.airSelf(it) }

val burnLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("burn").then(
        Commands.argument(
            "duration", IntegerArgumentType.integer(1, 120)
        ).then(
            Commands.argument(
                "targets", EntityArgument.players()
            ).executes { BurnCommand.burnOther(it) }
        ).executes { BurnCommand.burnSelf(it) }
    )

val feedLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("feed").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { FeedCommand.feedOther(it) }
    ).executes { FeedCommand.feedSelf(it) }

val healLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("heal").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { HealCommand.healOther(it) }
    ).executes { HealCommand.healSelf(it) }

val flyLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("fly").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { FlyCommand.flyOther(it) }
    ).executes { FlyCommand.flySelf(it) }

val godLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("god").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { GodCommand.godOther(it) }
    ).executes { GodCommand.godSelf(it) }

val helpLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("help").then(
        Commands.argument(
            "page", IntegerArgumentType.integer()
        ).executes { HelpCommand.process(it) }
    ).executes { HelpCommand.process(it) }

val lightningLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("lightning").then(
        Commands.argument(
            "targets", EntityArgument.entities()
        ).executes { LightningCommand.process(it) }
    )

val repairLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("repair").then(
        Commands.literal("all").executes { RepairCommand.repairAll(it) }
    ).executes { RepairCommand.repair(it) }

val sendPosLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("sendpos").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { SendPosCommand.process(it) }
    ).executes { SendPosCommand.process(it) }

val moreLiteral: LiteralArgumentBuilder<CommandSource> = literal("more")
val pingLiteral: LiteralArgumentBuilder<CommandSource> = literal("ping")
val suicideLiteral: LiteralArgumentBuilder<CommandSource> = literal("suicide")

// kit <list|get <kit-name> [[target] [cooldown clear]]>
val kitLiteral: LiteralArgumentBuilder<CommandSource> by lazy {
    literal<CommandSource>("kit").then(
        Commands.literal("get").then(
            Commands.argument(
                "kit-name", StringArrayArgument.with(KitManager.getKits().map { it.name })
            ).then(
                Commands.argument("target", EntityArgument.player()).then(
                    Commands.literal("cooldown").then(Commands.literal("clear")).executes {
                        KitCommand.kitClearCooldown(it)
                    }
                ).executes { KitCommand.kitOtherGet(it) }
            ).executes { KitCommand.kitGet(it) }
        )
    ).then(
        Commands.literal("list").then(
            Commands.argument(
                "page", IntegerArgumentType.integer()
            ).executes { KitCommand.kitList(it) }
        ).executes { KitCommand.kitList(it) }
    )
}
