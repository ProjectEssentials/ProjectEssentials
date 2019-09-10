package com.mairwunnx.projectessentials.commands

const val PERMISSION_LEVEL =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: permission level executing command more than player permission level."
    )
const val COOLDOWN_NOT_EXPIRED =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: command cooldown not expired."
    )
