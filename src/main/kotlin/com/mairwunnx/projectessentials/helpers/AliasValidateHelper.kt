package com.mairwunnx.projectessentials.helpers

val validateRegex = Regex("^\\D+\$")
fun validateAlias(alias: String): Boolean = validateRegex.matches(alias)
