package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.material.Material
import net.minecraft.command.CommandSource
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import kotlin.math.roundToInt

object BreakCommand : CommandBase() {
    private val logger = LogManager.getLogger()
    private var config = getCommandsConfig().commands.`break`

    init {
        command = "break"
        aliases = config.aliases.toMutableList()
    }

    override fun reload() {
        config = getCommandsConfig().commands.`break`
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
            if (PermissionsAPI.hasPermission(senderName, "ess.break")) {
                val maxDistance = 20
                for (i in 1..maxDistance) {
                    val eyePos = senderPlayer.getEyePosition(1.0f)
                    val lookVec = senderPlayer.getLook(1.0f)
                    val targetObjectPos = eyePos.add(
                        (lookVec.x * i).roundToInt().toDouble(),
                        (lookVec.y * i).roundToInt().toDouble(),
                        (lookVec.z * i).roundToInt().toDouble()
                    )
                    val blockPos = BlockPos(targetObjectPos.x, targetObjectPos.y, targetObjectPos.z)
                    val blockState = senderPlayer.world.getBlockState(blockPos)
                    val blockMaterial = blockState.material
                    if (blockMaterial != Material.AIR) {
                        when {
                            config.restrictedBlocks.contains(
                                blockState.block.registryName.toString()
                            ) && !PermissionsAPI.hasPermission(
                                senderName, "break.restricted.bypass"
                            ) -> sendMsg(sender, "break.restricted_block")
                            else -> sender.world.destroyBlock(blockPos, false)
                        }
                        break
                    }
                }
            } else {
                throwPermissionLevel(senderName, command)
                sendMsg(sender, "break.restricted", senderName)
                return 0
            }
        }

        logger.info("Executed command \"/$command\" from $senderName")
        return 0
    }
}
