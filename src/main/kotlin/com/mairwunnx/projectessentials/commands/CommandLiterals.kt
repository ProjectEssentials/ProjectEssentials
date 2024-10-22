package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.commands.teleport.*
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.managers.KitManager
import com.mairwunnx.projectessentials.managers.UserManager
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.BlockPosArgument
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

val kitLiteral: LiteralArgumentBuilder<CommandSource> by lazy {
    literal<CommandSource>("kit").then(
        Commands.argument("kit-name", StringArgumentType.string()).suggests { ctx, builder ->
            if (ctx.isPlayerSender()) {
                val player = ctx.getPlayer()!!
                ISuggestionProvider.suggest(
                    KitManager.getKits().filter {
                        KitManager.isKitExpired(
                            UserManager.getUserByNameOrUUID(
                                player.name.string, player.uniqueID.toString()
                            ) ?: return@filter true, it, player
                        ) && hasPermission(
                            player, "ess.kit.receive.${it.name}", it.requiredMinOpLevel
                        )
                    }.map { it.name }.toList(), builder
                )
            } else {
                ISuggestionProvider.suggest(
                    KitManager.getKits().map { it.name }.toList(), builder
                )
            }
        }.then(
            Commands.argument("target", EntityArgument.player()).executes {
                KitCommand.kitGetOther(it)
            }
        ).executes {
            KitCommand.kitGet(it)
        }
    ).then(
        Commands.literal("list").then(
            Commands.argument(
                "page", IntegerArgumentType.integer()
            ).executes { KitCommand.kitList(it) }
        ).executes { KitCommand.kitList(it) }
    )
}

val enderChestLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("enderchest").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { EnderChestCommand.openOther(it) }
    ).executes { EnderChestCommand.openSelf(it) }

val invSeeLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("invsee").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { InvSeeCommand.process(it) }
    )

val glowLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("glow").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { GlowCommand.glowOther(it) }
    ).executes { GlowCommand.glowSelf(it) }

val skullLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("skull").then(
        Commands.argument(
            "nick", StringArgumentType.string()
        ).executes { SkullCommand.skull(it) }
    )

val vanishLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("vanish").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { VanishCommand.vanishOther(it) }
    ).executes { VanishCommand.vanishSelf(it) }

val extLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("ext").then(
        Commands.argument(
            "targets", EntityArgument.players()
        ).executes { ExtCommand.extOther(it) }
    ).executes { ExtCommand.extSelf(it) }

val tpPosLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("tp-pos").then(
        Commands.argument(
            "position", BlockPosArgument.blockPos()
        ).executes { TpPosCommand.process(it) }
    )

val tpAllLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("tp-all").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { TpAllCommand.process(it) }
    )

val tpaLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("tpa").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { TpaCommand.process(it) }
    )

val tpaHereLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("tpa-here").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { TpaHereCommand.process(it) }
    )

val tpHereLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("tp-here").then(
        Commands.argument(
            "target", EntityArgument.player()
        ).executes { TpHereCommand.process(it) }
    )

val moreLiteral: LiteralArgumentBuilder<CommandSource> = literal("more")
val pingLiteral: LiteralArgumentBuilder<CommandSource> = literal("ping")
val suicideLiteral: LiteralArgumentBuilder<CommandSource> = literal("suicide")
val workbenchLiteral: LiteralArgumentBuilder<CommandSource> = literal("workbench")
val platformStatusLiteral: LiteralArgumentBuilder<CommandSource> = literal("platform-status")
val breakLiteral: LiteralArgumentBuilder<CommandSource> = literal("break")
val jumpLiteral: LiteralArgumentBuilder<CommandSource> = literal("jump")
val topLiteral: LiteralArgumentBuilder<CommandSource> = literal("top")
val tpaAllLiteral: LiteralArgumentBuilder<CommandSource> = literal("tpa-all")
val tpaCancelLiteral: LiteralArgumentBuilder<CommandSource> = literal("tpa-cancel")
val tpAcceptLiteral: LiteralArgumentBuilder<CommandSource> = literal("tp-accept")
val tpDenyLiteral: LiteralArgumentBuilder<CommandSource> = literal("tp-deny")
val tpToggleLiteral: LiteralArgumentBuilder<CommandSource> = literal("tp-toggle")
