package com.mairwunnx.projectessentials.states

import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

sealed class AfkState

class InAfk(val player: ServerPlayerEntity) : AfkState()

class AfkPresenter {
    private val logger = LogManager.getLogger()
    private var state: MutableList<AfkState> = mutableListOf()

    init {
        logger.info("Afk presenter has been initialized")
    }

    fun isInAfk(player: ServerPlayerEntity): Boolean {
        state.forEach {
            when (it) {
                is InAfk -> return it.player.name.string == player.name.string
            }
        }
        return false
    }

    fun setAfkPlayer(player: ServerPlayerEntity) {
        state.add(InAfk(player))
        logger.info("Afk state `true` has been committed for ${player.name.string}")
    }

    fun removeAfkPlayer(player: ServerPlayerEntity) {
        state.remove(InAfk(player))
        logger.info("Afk state `false` has been committed for ${player.name.string}")
    }
}
