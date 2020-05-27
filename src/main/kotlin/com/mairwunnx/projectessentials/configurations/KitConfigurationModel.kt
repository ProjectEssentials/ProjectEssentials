package com.mairwunnx.projectessentials.configurations

import kotlinx.serialization.Serializable

@Serializable
data class KitConfigurationModel(
    val kits: MutableList<String> = mutableListOf()
)
