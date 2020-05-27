package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.Serializable

@Serializable
data class KitsConfigurationModel(
    val kits: MutableList<String> = mutableListOf()
)
