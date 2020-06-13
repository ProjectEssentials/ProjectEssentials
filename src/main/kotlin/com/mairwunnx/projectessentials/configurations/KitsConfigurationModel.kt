package com.mairwunnx.projectessentials.configurations

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import kotlinx.serialization.Serializable

@Serializable
data class KitsConfigurationModel(
    val kits: MutableList<Kit> = mutableListOf(
        Kit(
            name = "newbies",
            delay = 43200,
            items = mutableListOf(
                Kit.Item("oak_wood", 8, "&7[&cStarter %player's Item&7]"),
                Kit.Item("cooked_cod", 5, "&7[&cStarter %player's Item&7]"),
                Kit.Item("cow_spawn_egg", 1, "&7[&cStarter %player's Item&7]"),
                Kit.Item("stone", 16, "&7[&cStarter %player's Item&7]"),
                Kit.Item("torch", 8, "&7[&cStarter %player's Item&7]")
            )
        )
    )
) {
    @Serializable
    data class Kit(
        var name: String,
        var delay: Int = 0,
        var requiredMinOpLevel: Int = 0,
        var items: MutableList<Item> = mutableListOf()
    ) {
        @Serializable
        data class Item(
            var name: String,
            var count: Int = 1,
            var displayName: String = String.empty,
            var enchantments: MutableList<Enchantment> = mutableListOf()
        ) {
            @Serializable
            data class Enchantment(
                var enchantment: String,
                var level: Int = 1
            )
        }
    }
}
