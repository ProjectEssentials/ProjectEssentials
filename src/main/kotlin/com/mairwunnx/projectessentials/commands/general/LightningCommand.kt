package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.effect.LightningBoltEntity
import org.apache.logging.log4j.LogManager

object LightningCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.lightning

    init {
        command = "lightning"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.lightning
        aliases = config.aliases.toMutableList()
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        aliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .then(
                    Commands.argument("entities", EntityArgument.entities()).executes {
                        return@executes execute(it)
                    }
                )
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        argument: Any?
    ): Int {
        super.execute(c, argument)

        if (PermissionsAPI.hasPermission(senderName, "ess.lightning", senderIsServer)) {
            val targets = EntityArgument.getEntities(c, "entities")

            targets.forEach {
                val lightning = LightningBoltEntity(
                    it.world, it.posX, it.posY, it.posZ, true
                )
                senderPlayer.serverWorld.addLightningBolt(lightning)
                it.onStruckByLightning(lightning)
            }

            if (senderIsServer) {
                if (targets.count() == 1) {
                    val target = targets.first()
                    logger.info("You smiting by lightning strike ${target.name.string} player or entity.")
                } else {
                    logger.info("You smiting by lightning strike entities and players: ${targets.count()}.")
                }
                return 0
            } else {
                if (targets.count() == 1) {
                    val target = targets.first()
                    sendMsg(sender, "lightning_entity.success", target.name.string)
                } else {
                    sendMsg(sender, "lightning_entities.success", targets.count().toString())
                }
            }
        } else {
            throwPermissionLevel(senderName, command)
            sendMsg(sender, "lightning.restricted", senderName)
            return 0
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
