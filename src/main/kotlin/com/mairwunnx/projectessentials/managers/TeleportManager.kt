package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.SETTING_TELEPORT_REQUEST_TIMEOUT
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.managers.TeleportRequestResponse.*
import org.apache.logging.log4j.LogManager
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

enum class TeleportAcceptRequest {}

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
    private val logger = LogManager.getLogger()
    private val requests = mutableListOf<ITeleportRequestType>()
    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    val requestSuppressPlayers = mutableListOf<String>()

    fun makeRequest(
        requestType: TeleportRequestType,
        requestInitiator: String,
        requestReceiver: String
    ): TeleportRequestResponse {
        purgeExpiredRequests()
        requests.find {
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

    private fun purgeExpiredRequests() {
        requests.removeAll {
            val initTime = it.requestDate
            val nowTime = ZonedDateTime.now()
            val duration = Duration.between(initTime, nowTime)
            duration.seconds > generalConfiguration.getInt(SETTING_TELEPORT_REQUEST_TIMEOUT)
        }
    }
}
