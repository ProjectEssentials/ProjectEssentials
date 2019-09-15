package com.mairwunnx.projectessentials.storage

import com.mairwunnx.projectessentials.extensions.empty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.util.Tuple

@Serializable
data class UserData(
    @SerialName("LastWorld")
    var lastWorld: String = String.empty,
    @SerialName("LastWorldPos")
    var lastWorldPos: String = String.empty,
    @SerialName("Worlds")
    var worlds: List<World> = listOf()
) {
    @Serializable
    data class World(
        @SerialName("WorldName")
        var worldName: String = String.empty,
        @SerialName("FlyModeEnabled")
        var flyModeEnabled: Boolean = false,
        @SerialName("GoodModeEnabled")
        var goodModeEnabled: Boolean = false
    ) {
        fun containsIn(collection: List<World>): Tuple<Int, Boolean> {
            for ((i, world) in collection.withIndex()) {
                if (world.worldName == this.worldName) return Tuple(i, true)
            }
            return Tuple(-1, false)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as World

            if (worldName != other.worldName) return false
            if (flyModeEnabled != other.flyModeEnabled) return false
            if (goodModeEnabled != other.goodModeEnabled) return false

            return true
        }

        override fun hashCode(): Int {
            var result = worldName.hashCode()
            result = 31 * result + flyModeEnabled.hashCode()
            result = 31 * result + goodModeEnabled.hashCode()
            return result
        }

        fun equalsName(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as World

            if (worldName != other.worldName) return false

            return true
        }
    }

}
