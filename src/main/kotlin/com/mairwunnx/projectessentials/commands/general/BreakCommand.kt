package com.mairwunnx.projectessentials.commands.general

import com.mairwunnx.projectessentials.commands.CommandBase
import com.mairwunnx.projectessentials.configurations.CommandsConfig
import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.UnstableDefault
import net.minecraft.block.material.Material
import net.minecraft.command.CommandSource
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import kotlin.math.roundToInt

@UnstableDefault
object BreakCommand : CommandBase<CommandsConfig.Commands.Break>(
    getCommandsConfig().commands.`break`,
    hasArguments = false
) {
    private val logger = LogManager.getLogger()

    override fun reload() {
        commandInstance = getCommandsConfig().commands.`break`
        super.reload()
    }

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        commandAliases.forEach { command ->
            dispatcher.register(literal<CommandSource>(command)
                .executes {
                    execute(it)
                    return@executes 0
                }
            )
        }
    }

    override fun execute(
        c: CommandContext<CommandSource>,
        hasTarget: Boolean
    ): Boolean {
        val code = super.execute(c, hasTarget)
        if (!code) return false

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
                    config.commands.`break`.restrictedBlocks.contains(
                        blockState.block.registryName.toString()
                    ) && !senderPlayer.hasPermissionLevel(
                        config.commands.`break`.restrictedBlockByPassPermLevel
                    ) -> sendMsg(sender, "break.restricted")
                    else -> sender.world.destroyBlock(blockPos, false)
                }
                break
            }
        }

        logger.info("Executed command \"/$commandName\" from $senderNickName")
        return true
    }
}
