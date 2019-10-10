package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.helpers.ONLY_PLAYER_CAN
import com.mairwunnx.projectessentialscore.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.Hand
import org.apache.logging.log4j.LogManager

object RepairCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.repair

    init {
        command = "repair"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.repair
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(Commands.literal("all").executes {
                    return@executes execute(it, "all")
                })
                .then(Commands.literal("hand").executes {
                    return@executes execute(it)
                })
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
            logger.warn(ONLY_PLAYER_CAN.replace("%0", command))
            return 0
        } else {
            if (argument is String && argument == "all") {
                if (PermissionsAPI.hasPermission(senderName, "ess.repair.all")) {
                    val inventory = senderPlayer.inventory
                    inventory.armorInventory.forEach {
                        if (it.isDamaged) it.damage = 0
                    }
                    inventory.mainInventory.forEach {
                        if (it.isDamaged) it.damage = 0
                    }
                    inventory.offHandInventory.forEach {
                        if (it.isDamaged) it.damage = 0
                    }
                    sendMsg(sender, "repair.all.out")
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "repair.all.restricted", senderName)
                    return 0
                }
            } else {
                if (PermissionsAPI.hasPermission(senderName, "ess.repair")) {
                    val item = senderPlayer.getHeldItem(Hand.MAIN_HAND)
                    if (item.isDamaged) {
                        item.damage = 0
                        sendMsg(sender, "repair.out")
                    } else {
                        sendMsg(sender, "repair.fulldamage")
                    }
                } else {
                    logger.warn(
                        PERMISSION_LEVEL
                            .replace("%0", senderName)
                            .replace("%1", command)
                    )
                    sendMsg(sender, "repair.restricted", senderName)
                    return 0
                }
            }
        }
        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
