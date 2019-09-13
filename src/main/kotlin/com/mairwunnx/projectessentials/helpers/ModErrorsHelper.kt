package com.mairwunnx.projectessentials.helpers

const val PERMISSION_LEVEL =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: permission level executing command more than player permission level."
    )
const val COOLDOWN_NOT_EXPIRED =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: command cooldown not expired."
    )
const val DISABLED_COMMAND =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: it command disabled by mod configuration."
    )
const val DISABLED_COMMAND_ARG =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: arguments for it command disabled by mod configuration."
    )
const val ONLY_PLAYER_CAN =
    "Server failed to executing \"/%0\" command".plus(
        "\n    - Reason: command should only be used by the player."
    )
