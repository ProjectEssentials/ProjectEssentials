package com.mairwunnx.projectessentials.states

import com.mairwunnx.projectessentials.configurations.ModConfiguration
import net.minecraft.entity.player.ServerPlayerEntity
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
) : TeleportState() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Requested

        if (playerRequested != other.playerRequested) return false
        if (targetPlayer != other.targetPlayer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerRequested.hashCode()
        result = 31 * result + targetPlayer.hashCode()
        return result
    }
}

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
    var ignoredPlayers: MutableList<String> = mutableListOf() // todo: it need save
    private var timeOut = 45

    fun configureTimeOut() {
        timeOut = ModConfiguration.getCommandsConfig().commands.tpa.timeOut
    }

    /**
     * @return true if request added successfully,
     * false if request adding failed (e.g player already
     * maked request to any player).
     */
    fun commitRequest(
        playerRequested: ServerPlayerEntity,
        targetPlayer: ServerPlayerEntity
    ): Boolean {
        removeExpiredRequest()

        state.forEach {
            if (it is Requested) when (it) {
                Requested(playerRequested, targetPlayer, ZonedDateTime.now()) -> return false
            }
        }

        if (targetPlayer.name.string in ignoredPlayers) return false

        state.add(Requested(playerRequested, targetPlayer, ZonedDateTime.now()))
        return true
    }

    /**
     * It should be return player if request exists,
     * returned player can be used for identification request
     * maker, for getting late player position, for teleport
     * to you.
     *
     * @return data type ServerPlayerEntity (nullable),
     * if return null then request expired or not exist.
     * if request not expired or exists then return player
     * who make request.
     */
    fun getRequest(
        player: ServerPlayerEntity
    ): ServerPlayerEntity? {
        removeExpiredRequest()

        return (this.state.find {
            if (it is Requested) {
                if (player.name.string == it.targetPlayer.name.string) {
                    return@find true
                }
            }
            return@find false
        } as Requested?)?.playerRequested
    }

    fun removeExpiredRequest() {
        removeRequestedExpiredRequest()
    }

    /**
     * Just remove all expired requests from teleport state.
     * It method remove only `Requested` type requests.
     */
    @UseExperimental(ExperimentalTime::class)
    private fun removeRequestedExpiredRequest() {
        state.removeAll {
            it as Requested

            val duration = Duration.between(it.requestTime, ZonedDateTime.now())
            val passedSeconds = duration.toKotlinDuration().inSeconds

            return@removeAll (passedSeconds > timeOut)
        }
    }

    /**
     * @param requestInitiator request initiator as player,
     * i.e player who created request.
     * @param requested requested player, i.e player who can
     * accept or deny request.
     * @return true if request removed successfully,
     * false if request not exists (i.e not able for removing).
     */
    fun removeRequest(
        requestInitiator: ServerPlayerEntity,
        requested: ServerPlayerEntity
    ): Boolean {
        return state.removeIf {
            it as Requested
            requestInitiator == it.playerRequested && requested == it.targetPlayer
        }
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
