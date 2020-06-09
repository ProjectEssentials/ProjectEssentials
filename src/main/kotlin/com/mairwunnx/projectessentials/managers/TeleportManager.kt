package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.SETTING_TELEPORT_REQUEST_TIMEOUT
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.findPlayer
import com.mairwunnx.projectessentials.generalConfiguration
import com.mairwunnx.projectessentials.managers.TeleportAcceptRequestResponse.*
import com.mairwunnx.projectessentials.managers.TeleportRemoveRequestResponse.NothingToRemove
import com.mairwunnx.projectessentials.managers.TeleportRemoveRequestResponse.Success
import com.mairwunnx.projectessentials.managers.TeleportRequestResponse.*
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer
import java.time.Duration
import java.time.ZonedDateTime

interface ITeleportRequestType {
    val requestInitiator: String
    val requestReceiver: String
    val requestDate: ZonedDateTime
}

enum class TeleportRequestType { To, From }

enum class TeleportRequestResponse {
    SuchRequestExist,
    RequestedIsIgnored,
    RequestToSuccess,
    RequestFromSuccess
}

enum class TeleportAcceptRequestResponse {
    NothingToAccept,
    AcceptedToSuccessful,
    AcceptedHereSuccessful,
    RequestedPlayerOffline
}

enum class TeleportRemoveRequestResponse {
    Success, NothingToRemove
}

enum class TeleportRequestAllResponse {
    Success, NothingToRequest
}

data class TeleportRequestTo(
    override val requestInitiator: String,
    override val requestReceiver: String,
    override val requestDate: ZonedDateTime
) : ITeleportRequestType

data class TeleportRequestFrom(
    override val requestInitiator: String,
    override val requestReceiver: String,
    override val requestDate: ZonedDateTime
) : ITeleportRequestType

object TeleportManager {
    private val requests = mutableListOf<ITeleportRequestType>()

    val requestSuppressPlayers = mutableListOf<String>()

    fun makeRequest(
        requestType: TeleportRequestType,
        requestInitiator: String,
        requestReceiver: String
    ): TeleportRequestResponse {
        purgeExpiredRequests()
        requests.asSequence().find {
            it.requestInitiator == requestInitiator && it.requestReceiver == requestReceiver
        }?.let {
            if (requestType == TeleportRequestType.To && it is TeleportRequestTo) return SuchRequestExist
            if (requestType == TeleportRequestType.From && it is TeleportRequestFrom) return SuchRequestExist
        }
        if (requestReceiver in requestSuppressPlayers) return RequestedIsIgnored
        if (requestType == TeleportRequestType.To) {
            requests.add(
                TeleportRequestTo(requestInitiator, requestReceiver, ZonedDateTime.now())
            ).also { return RequestToSuccess }
        } else {
            requests.add(
                TeleportRequestFrom(requestInitiator, requestReceiver, ZonedDateTime.now())
            ).also { return RequestFromSuccess }
        }
    }

    fun makeRequestToAll(
        requestType: TeleportRequestType,
        requestInitiator: String
    ): TeleportRequestAllResponse {
        getCurrentServer().playerList.players.asSequence().filter {
            it.name.string !in requestSuppressPlayers && it.name.string != requestInitiator
        }.also {
            if (it.count() <= 1) return TeleportRequestAllResponse.NothingToRequest
        }.forEach {
            val result = makeRequest(requestType, requestInitiator, it.name.string)
            if (result == RequestToSuccess) {
                MessagingAPI.sendMessage(
                    it,
                    "${MESSAGE_MODULE_PREFIX}basic.tpa.receiver.to",
                    args = *arrayOf(requestInitiator)
                )
            } else if (result == RequestFromSuccess) {
                MessagingAPI.sendMessage(
                    it,
                    "${MESSAGE_MODULE_PREFIX}basic.tpa.receiver.from",
                    args = *arrayOf(requestInitiator)
                )
            }
        }
        return TeleportRequestAllResponse.Success
    }

    fun takeRequest(
        requestAcceptor: String
    ): Pair<TeleportAcceptRequestResponse, ServerPlayerEntity?> {
        purgeExpiredRequests()
        requests.findLast {
            it.requestReceiver == requestAcceptor
        }?.let {
            getCurrentServer().findPlayer(it.requestInitiator)?.let { requestInitiator ->
                return if (it is TeleportRequestTo) {
                    removeRequest(TeleportRequestType.To, it.requestInitiator, requestAcceptor)
                    Pair(AcceptedToSuccessful, requestInitiator)
                } else {
                    removeRequest(TeleportRequestType.From, it.requestInitiator, requestAcceptor)
                    Pair(AcceptedHereSuccessful, requestInitiator)
                }
            } ?: run { return Pair(RequestedPlayerOffline, null) }
        } ?: run { return Pair(NothingToAccept, null) }
    }

    fun removeRequest(
        requestType: TeleportRequestType,
        requestInitiator: String,
        requestReceiver: String
    ) {
        requests.removeAll {
            when {
                requestType == TeleportRequestType.To && it is TeleportRequestTo -> {
                    it.requestReceiver == requestReceiver && it.requestInitiator == requestInitiator
                }
                requestType == TeleportRequestType.From && it is TeleportRequestFrom -> {
                    it.requestReceiver == requestReceiver && it.requestInitiator == requestInitiator
                }
                else -> false
            }
        }
    }

    fun removeLastRequest(requestInitiator: String): TeleportRemoveRequestResponse {
        purgeExpiredRequests()
        requests.findLast { it.requestInitiator == requestInitiator }?.let {
            requests.remove(it)
            return Success
        } ?: run { return NothingToRemove }
    }

    private fun purgeExpiredRequests() {
        requests.removeAll {
            val initTime = it.requestDate
            val nowTime = ZonedDateTime.now()
            val duration = Duration.between(initTime, nowTime)
            duration.seconds > generalConfiguration.getInt(SETTING_TELEPORT_REQUEST_TIMEOUT)
        }
    }
}
