package com.mairwunnx.projectessentials.states

import com.mairwunnx.projectessentials.configurations.ModConfiguration.getCommandsConfig
import com.mairwunnx.projectessentials.extensions.findPlayer
import com.mairwunnx.projectessentials.extensions.sendMsg
import com.mairwunnx.projectessentialscore.extensions.empty
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

/**
 * Base teleport state class.
 */
sealed class TeleportState

/**
 * @param requestInitiator player nickname, who created
 * request, i.e who initiator of request.
 * @param requestedPlayer target of request, i.e player
 * who can accept or decine teleport request.
 * @param requestTime teleport request creating time.
 */
class Requested(
    val requestInitiator: String,
    val requestedPlayer: String,
    val requestTime: ZonedDateTime
) : TeleportState() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Requested

        if (requestInitiator != other.requestInitiator) return false
        if (requestedPlayer != other.requestedPlayer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestInitiator.hashCode()
        result = 31 * result + requestedPlayer.hashCode()
        return result
    }
}

/**
 * @param requestInitiator player nickname, who created
 * request, i.e who initiator of request.
 * @param requestedPlayer target of request, i.e player
 * who can accept or decine teleport request.
 * @param requestTime teleport request creating time.
 */
class RequestedHere(
    val requestInitiator: String,
    val requestedPlayer: String,
    val requestTime: ZonedDateTime
) : TeleportState() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestedHere

        if (requestInitiator != other.requestInitiator) return false
        if (requestedPlayer != other.requestedPlayer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestInitiator.hashCode()
        result = 31 * result + requestedPlayer.hashCode()
        return result
    }
}

/**
 * @param server minecraft server instance, basically
 * needed for finding player by nickname on server.
 */
class TeleportPresenter(private val server: MinecraftServer) {
    private val logger = LogManager.getLogger()
    private val state: MutableList<TeleportState> = mutableListOf()
    /**
     * Stores data of ignored players, i.e players who
     * disabled teleport request thought using command `/tptoggle`.
     */
    var ignoredPlayers: MutableList<String> = mutableListOf() // todo: it need save
    /**
     * Time out of teleport request, after expiring some time
     * (able to configure in configuration) teleport request will be
     * removed.
     */
    private var timeOut = 45

    init {
        logger.info("Initializing teleport presenter")
    }

    fun configureTimeOut() {
        timeOut = getCommandsConfig().commands.tpa.timeOut
    }

    /**
     * @return true if request added successfully,
     * false if request adding failed (e.g player already
     * maked request to any player) or player toggled off
     * teleport requests.
     */
    fun commitRequest(
        requestInitiator: String,
        requestedPlayer: String
    ): Boolean {
        removeExpiredRequest()

        state.forEach {
            if (it is Requested &&
                it == Requested(requestInitiator, requestedPlayer, ZonedDateTime.now())
            ) return false
        }

        if (requestedPlayer in ignoredPlayers) return false

        state.add(Requested(requestInitiator, requestedPlayer, ZonedDateTime.now()))
        return true
    }

    /**
     * It should be return player if request exists,
     * returned player can be used for identification request
     * maker, for getting late player position, for teleport
     * to you.
     *
     * @param player target player nickname, i.e player what take
     * request. (i.e not request initiator).
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

        (this.state.find {
            if (it is Requested) {
                if (player.name.string == it.requestedPlayer) {
                    return@find true
                }
            }
            return@find false
        } as Requested?)?.requestInitiator.let {
            return server.findPlayer(it ?: String.empty)
        }
    }

    /**
     * @param requestInitiator request initiator player nickname,
     * i.e player who created request.
     * @param requestedPlayer requested player, i.e player who can
     * accept or deny request.
     * @return true if request removed successfully,
     * false if request not exists (i.e not able for removing).
     */
    fun removeRequest(
        requestInitiator: String,
        requestedPlayer: String
    ): Boolean {
        return state.removeIf {
            if (it is Requested) {
                return@removeIf requestInitiator == it.requestInitiator &&
                        requestedPlayer == it.requestedPlayer
            }
            return@removeIf false
        }
    }

    /**
     * @return true if request added successfully,
     * false if request adding failed (e.g player already
     * maked request to any player) or player toggled off
     * teleport requests.
     */
    fun commitRequestHere(
        requestInitiator: String,
        requestedPlayer: String
    ): Boolean {
        removeExpiredRequest()

        state.forEach {
            if (it is RequestedHere &&
                it == RequestedHere(requestInitiator, requestedPlayer, ZonedDateTime.now())
            ) return false
        }

        if (requestedPlayer in ignoredPlayers) return false

        state.add(RequestedHere(requestInitiator, requestedPlayer, ZonedDateTime.now()))
        return true
    }

    /**
     * It should be return player if request exists,
     * returned player can be used for identification request
     * maker, for getting late player position, for teleport
     * to player who make request.
     *
     * @param player target player nickname, i.e player what take
     * request for teleport to player. (i.e not request initiator).
     *
     * @return data type ServerPlayerEntity (nullable),
     * if return null then request expired or not exist.
     * if request not expired or exists then return player
     * who make request.
     */
    fun getRequestHere(
        player: ServerPlayerEntity
    ): ServerPlayerEntity? {
        removeExpiredRequest()

        (this.state.find {
            if (it is RequestedHere) {
                if (player.name.string == it.requestedPlayer) {
                    return@find true
                }
            }
            return@find false
        } as RequestedHere?)?.requestInitiator.let {
            return server.findPlayer(it ?: String.empty)
        }
    }

    fun removeRequestHere(
        requestInitiator: String,
        requestedPlayer: String
    ): Boolean {
        return state.removeIf {
            if (it is RequestedHere) {
                return@removeIf requestInitiator == it.requestInitiator &&
                        requestedPlayer == it.requestedPlayer
            }
            return@removeIf false
        }
    }

    fun makeRequestToAll(
        requestInitiator: String,
        players: Collection<ServerPlayerEntity>
    ) {
        var server: MinecraftServer? = null
        players.forEach {
            if (server == null) server = it.server
            if (it.name.string != requestInitiator) {
                commitRequest(requestInitiator, it.name.string)
                sendMsg(it.commandSource, "request from $requestInitiator")
            }
        }
        if (server != null) {
            val initiator = server!!.findPlayer(requestInitiator)
            if (initiator != null) {
                sendMsg(initiator.commandSource, "request success")
            }
        }
    }

    /**
     * Remove all requests, tpa, tpahere and other.
     *
     * @param requestInitiator request initiator player nickname,
     * i.e player who created request.
     * @return true if request removed successfully,
     * false if request not exists (i.e not able for removing).
     */
    fun removeAllRequests(
        requestInitiator: String
    ): Boolean {
        return state.removeAll {
            when (it) {
                is Requested -> {
                    return@removeAll requestInitiator == it.requestInitiator
                }
                is RequestedHere -> {
                    return@removeAll requestInitiator == it.requestInitiator
                }
                else -> return@removeAll false
            }
        }
    }

    private fun removeExpiredRequest() {
        removeRequestedExpiredRequest()
        removeRequestedHereExpiredRequest()
    }

    /**
     * Just remove all expired requests from teleport state.
     * It method remove only `Requested` type requests.
     */
    @UseExperimental(ExperimentalTime::class)
    private fun removeRequestedExpiredRequest() {
        state.removeAll {
            if (it is Requested) {
                val duration = Duration.between(it.requestTime, ZonedDateTime.now())
                val passedSeconds = duration.toKotlinDuration().inSeconds

                return@removeAll (passedSeconds > timeOut)
            }

            return@removeAll false
        }
    }

    /**
     * Just remove all expired requests from teleport state.
     * It method remove only `RequestedHere` type requests.
     */
    @UseExperimental(ExperimentalTime::class)
    private fun removeRequestedHereExpiredRequest() {
        state.removeAll {
            if (it is RequestedHere) {
                val duration = Duration.between(it.requestTime, ZonedDateTime.now())
                val passedSeconds = duration.toKotlinDuration().inSeconds

                return@removeAll (passedSeconds > timeOut)
            }

            return@removeAll false
        }
    }
}
