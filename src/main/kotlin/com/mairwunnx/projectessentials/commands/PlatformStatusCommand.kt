package com.mairwunnx.projectessentials.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.validateAndExecute
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.commons.lang3.time.DurationFormatUtils
import java.lang.Runtime.getRuntime
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.min

object PlatformStatusCommand : CommandBase(platformStatusLiteral) {
    private val timeFormatter = DecimalFormat("########0.000")

    override val name = "platform-status"
    override val aliases = listOf("gc", "ram", "ps", "status", "monitor", "monit")

    override fun process(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.platformstatus", 0) { isServer ->
            val message = StringBuilder(String.empty)

            message.append("§6Uptime: ")
            DurationFormatUtils.formatDuration(
                ManagementFactory.getRuntimeMXBean().uptime, "d:H:m:s"
            ).split(':').asSequence().forEachIndexed { index, value ->
                message.append(
                    StringBuilder(String.empty).apply {
                        when (index) {
                            0 -> if (value != "0") append("§7$value §cdays ")
                            1 -> if (value != "0" || contains("days")) append("§7$value §chours ")
                            2 -> if (value != "0" || contains("hours")) append("§7$value §cminutes ")
                            3 -> append("§7$value §cseconds")
                        }
                    }.toString()
                )
            }.also { message.append("\n") }

            val tickTime = mean(context.source.server.tickTimeArray) * 1.0E-6
            val tps = min(1000.0 / tickTime, 20.0)
            val tpsFormatted = timeFormatter.format(tps)
            message.append("§6Current TPS: ")
            when {
                tps >= 18.0 -> message.append("§a$tpsFormatted\n")
                tps >= 15.0 -> message.append("§e$tpsFormatted\n")
                tps >= 14.0 -> message.append("§6$tpsFormatted\n")
                else -> message.append("§c$tpsFormatted\n")
            }

            fun formatNumber(num: Number) = NumberFormat.getInstance().format(num)
            val maxMemory = formatNumber(getRuntime().maxMemory() / 1024 / 1024)
            val allocatedMemory = formatNumber(getRuntime().totalMemory() / 1024 / 1024)
            val freeMemory = formatNumber(getRuntime().freeMemory() / 1024 / 1024)
            message.append("§6Maximum memory: §7$maxMemory §cMB\n")
            message.append("§6Allocated memory: §7$allocatedMemory §cMB\n")
            message.append("§6Free memory: §7$freeMemory §cMB\n")

            message.append(
                StringBuilder(String.empty).apply {
                    append("§6Loaded worlds:\n")
                    context.source.server.worlds.asSequence().forEach {
                        val loadedChunks = formatNumber(it.chunkProvider.loadedChunkCount)
                        val tileEntities = formatNumber(it.loadedTileEntityList.count())
                        val entities = formatNumber(it.entities.count())
                        append(
                            "    §7> §6(§7${it.dimension.type.registryName}§6/§7${it.dimension.type.id}§6): §7$loadedChunks §cchunks, §7$tileEntities §ctiles, §7$entities §centities\n"
                        )
                    }
                }.toString()
            )

            when {
                isServer -> ServerMessagingAPI.response { "\n$message" }
                else -> MessagingAPI.sendMessage(context.getPlayer()!!, message.toString(), false)
            }
            if (!isServer) super.process(context)
        }
    }

    private fun mean(values: LongArray): Long {
        var sum = 0L
        for (v in values) sum += v
        return sum / values.size
    }
}
