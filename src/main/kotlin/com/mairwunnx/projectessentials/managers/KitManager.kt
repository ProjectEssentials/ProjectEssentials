@file:Suppress("MemberVisibilityCanBePrivate")

package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.ModuleObject
import com.mairwunnx.projectessentials.configurations.KitsConfiguration
import com.mairwunnx.projectessentials.configurations.KitsConfigurationModel
import com.mairwunnx.projectessentials.configurations.UserDataConfiguration
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.StringReader
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentUtils
import net.minecraftforge.registries.ForgeRegistries
import java.time.Duration
import java.time.ZonedDateTime

object KitManager {
    enum class Response { KitNotFound, KitNoHasPermissions, KitTimeNotExpired, Success }

    private val userDataConfiguration by lazy {
        getConfigurationByName<UserDataConfiguration>("user-data")
    }

    private val kitsConfiguration by lazy {
        getConfigurationByName<KitsConfiguration>("kits")
    }

    fun isKitExist(name: String) = name in kitsConfiguration.take().kits.map { it.name }

    fun getKit(name: String) = kitsConfiguration.take().kits.find { it.name == name }

    fun getKits() = kitsConfiguration.take().kits

    fun requestKit(playerEntity: ServerPlayerEntity, name: String): Response {
        if (!isKitExist(name)) return Response.KitNotFound
        val kit = getKit(name)!!

        if (hasPermission(playerEntity, "ess.kit.receive.kit.$name", kit.requiredMinOpLevel)) {
            userDataConfiguration.take().users.find {
                it.name == playerEntity.name.string || it.uuid == playerEntity.uniqueID.toString()
            }?.let { user ->
                user.lastKitsDates.map { value ->
                    value.split('@').let { it[0] to it[1] }
                }.find { it.first == name }?.let {
                    val lastTime = ZonedDateTime.parse(it.second)
                    val nowTime = ZonedDateTime.now()
                    val duration = Duration.between(lastTime, nowTime)
                    if (kit.delay > duration.seconds) {
                        if (
                            hasPermission(
                                playerEntity, "ess.kit.receive.kit.$name.cooldown.bypass", 4
                            )
                        ) {
                            return Response.Success.also { unpackKit(playerEntity, kit) }
                        }
                        return Response.KitTimeNotExpired
                    } else {
                        return Response.Success.also { unpackKit(playerEntity, kit) }
                    }
                } ?: run {
                    return Response.Success.also { unpackKit(playerEntity, kit) }
                }
            } ?: run {
                return Response.Success.also { unpackKit(playerEntity, kit) }
            }
        }
        return Response.KitNoHasPermissions
    }

    private fun unpackKit(receiver: ServerPlayerEntity, kit: KitsConfigurationModel.Kit) {
        kit.items.forEach { kitItem ->
            if (kitItem.name.isNotBlank()) {
                val item = checkIllegalItem(kitItem.name)
                if (item != null) {
                    ItemStack(
                        item, checkIllegalItemCount(kitItem.count)
                    ).apply {
                        if (kitItem.displayName.isNotBlank()) {
                            displayName = TextComponentUtils.toTextComponent {
                                kitItem.displayName
                                    .replace("&", "§")
                                    .replace("%player", receiver.name.string)
                                    .replace("%kit", kit.name)
                            }
                        }
                        kitItem.enchantments.forEach {
                            if (it.enchantment.isNotBlank()) {
                                val enchantment = checkIllegalEnchantment(it.enchantment)
                                if (enchantment != null) {
                                    addEnchantment(enchantment, checkIllegalEnchantLevel(it.level))
                                }
                            }
                        }
                    }.also { receiver.addItemStackToInventory(it) }
                }
            }
        }
        if (
            !hasPermission(receiver, "ess.kit.receive.kit.${kit.name}.cooldown.bypass", 4) ||
            kit.delay != 0
        ) markAsTaken(receiver, kit.name)
    }

    private fun checkIllegalItem(item: String) =
        ForgeRegistries.ITEMS.getValue(
            ResourceLocation.read(StringReader(item))
        )?.item

    private fun checkIllegalEnchantment(enchantment: String) =
        ForgeRegistries.ENCHANTMENTS.getValue(
            ResourceLocation.read(StringReader(enchantment))
        )

    private fun checkIllegalItemCount(count: Int) = when {
        count < 1 -> 1
        count > 64 -> 64
        else -> count
    }

    private fun checkIllegalEnchantLevel(level: Int) = when {
        level < 1 -> 1
        level > 25 -> 25
        else -> level
    }

    private fun markAsTaken(receiver: ServerPlayerEntity, kitName: String) {
        (ModuleAPI.getModuleByName("basic") as ModuleObject).savePlayerData(receiver)
        userDataConfiguration.take().users.find {
            it.name == receiver.name.string || it.uuid == receiver.uniqueID.toString()
        }?.let { user ->
            user.lastKitsDates.map { value ->
                value.split('@').let { it[0] to it[1] }
            }.find { it.first == kitName }?.let { expiredKit ->
                user.lastKitsDates.removeAll { expiredKit.first in it }
            }
            user.lastKitsDates.add("$kitName@${ZonedDateTime.now()}")
        }
    }
}
