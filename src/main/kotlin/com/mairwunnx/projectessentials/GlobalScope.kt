package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.configurations.KitsConfiguration
import com.mairwunnx.projectessentials.configurations.UserDataConfiguration
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName

val userDataConfiguration by lazy {
    getConfigurationByName<UserDataConfiguration>("user-data").take()
}

val kitsConfiguration by lazy {
    getConfigurationByName<KitsConfiguration>("kits").take()
}
