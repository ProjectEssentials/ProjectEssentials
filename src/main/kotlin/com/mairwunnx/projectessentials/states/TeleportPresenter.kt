package com.mairwunnx.projectessentials.states

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.Tuple
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

sealed class TeleportState

class Requested(
    val playerRequested: ServerPlayerEntity,
    val targetPlayer: ServerPlayerEntity,
    val requestTime: ZonedDateTime
) : TeleportState()

class RequestedToAll(
    val playerRequested: ServerPlayerEntity,
    val targetPlayer: List<ServerPlayerEntity>,
    val requestTime: ZonedDateTime
) : TeleportState()

class RequestedHere(
    val playerRequested: ServerPlayerEntity,
    val targetPlayer: ServerPlayerEntity,
    val requestTime: ZonedDateTime
) : TeleportState()

class TeleportPresenter {
    private val logger = LogManager.getLogger()
    private val state: MutableList<TeleportState> = mutableListOf()
    var ignoredPlayers: MutableList<String> = mutableListOf()
    var timeOut = 45

    fun commitRequest(
        playerRequested: ServerPlayerEntity,
        targetPlayer: ServerPlayerEntity
    ): Boolean {
        state.forEach {
            if ((it as Requested).targetPlayer.name.string == targetPlayer.name.string) {
                return false
            }
        }
        state.add(Requested(playerRequested, targetPlayer, ZonedDateTime.now()))
        return true
    }

    @UseExperimental(ExperimentalTime::class)
    fun getRequest(
        player: ServerPlayerEntity
    ): Tuple<ServerPlayerEntity?, ServerPlayerEntity?> {
        val state = state.find {
            if (it is Requested) {
                if (player.name.string == it.targetPlayer.name.string) {
                    return@find true
                }
            }
            return@find false
        } as Requested

        val duration = Duration.between(state.requestTime, ZonedDateTime.now())
        val passedSeconds = duration.toKotlinDuration().inSeconds

        if (passedSeconds < timeOut) {
            return Tuple(state.playerRequested, state.playerRequested)
        } else {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            return Tuple(null, null)
        }
    }

    fun removeRequest() {

    }

    fun commitRequestToAll() {

    }

    fun getRequestToAll() {

    }

    fun removeRequestToAll() {

    }

    fun commitRequestHere() {

    }

    fun getRequestHere() {

    }

    fun removeRequestHere() {

    }
}
