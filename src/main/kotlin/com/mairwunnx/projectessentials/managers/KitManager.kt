@file:Suppress("MemberVisibilityCanBePrivate")

package com.mairwunnx.projectessentials.managers

import com.mairwunnx.projectessentials.ModuleObject
import com.mairwunnx.projectessentials.configurations.KitsConfigurationModel.Kit
import com.mairwunnx.projectessentials.configurations.UserDataConfigurationModel
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.kitsConfiguration
import com.mairwunnx.projectessentials.userDataConfiguration
import com.mojang.brigadier.StringReader
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentUtils
import net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS
import net.minecraftforge.registries.ForgeRegistries.ITEMS
import java.time.Duration
import java.time.ZonedDateTime

object KitManager {
    enum class Response { KitNotFound, KitNoHasPermissions, KitTimeNotExpired, Success }

    fun getKits() = kitsConfiguration.kits.asSequence()
    fun getKit(name: String) = getKits().find { it.name == name }
    fun isKitExist(name: String) = getKits().filter { it.name == name }.count() > 0
    fun getKitItems(kit: Kit) = kit.items.asSequence()

    fun requestKit(
        from: ServerPlayerEntity, to: ServerPlayerEntity, name: String
    ): Response {
        if (!isKitExist(name)) return Response.KitNotFound
        val kit = getKit(name)!!
        if (hasPermission(from, permissionOf(name), kit.requiredMinOpLevel)) {
            userDataConfiguration.users.find {
                it.name == from.name.string || it.uuid == from.uniqueID.toString()
            }?.let { user ->
                if (isKitExpired(user, kit, from)) {
                    unpackKit(from, to, kit).let { return Response.Success }
                } else return Response.KitTimeNotExpired
            } ?: run { return Response.Success.also { unpackKit(from, to, kit) } }
        } else return Response.KitNoHasPermissions
    }

    fun isKitExpired(
        user: UserDataConfigurationModel.User, kit: Kit, target: ServerPlayerEntity
    ) = user.lastKitsDates.map { value ->
        value.split('@').let { it[0] to it[1] }
    }.find { it.first == kit.name }?.let {
        val duration = Duration.between(ZonedDateTime.parse(it.second), ZonedDateTime.now())
        if (kit.delay > duration.seconds) {
            return@let hasPermission(target, "ess.kit.bypass", 4)
        } else return@let true
    } ?: let { return@let true }

    private fun unpackKit(
        sender: ServerPlayerEntity, receiver: ServerPlayerEntity, kit: Kit
    ) = getKitItems(kit).forEach { kitItem ->
        nullIfIllegalItem(kitItem.name)?.also { item ->
            ItemStack(item, fixItemCount(kitItem.count)).apply {
                if (kitItem.displayName.isNotBlank()) {
                    displayName = TextComponentUtils.toTextComponent {
                        kitItem.displayName
                            .replace("&", "§")
                            .replace("%player", sender.name.string)
                            .replace("%kit", kit.name)
                    }
                }
                kitItem.enchantments.forEach {
                    nullIfIllegalEnchantment(it.enchantment)?.also { ench ->
                        addEnchantment(ench, fixEnchantLevel(it.level))
                    }
                }
            }.also { receiver.addItemStackToInventory(it) }
        }
    }.also {
        if (!hasPermission(sender, "ess.kit.bypass", 4) || kit.delay != 0) {
            markAsTaken(sender, kit.name)
        }
    }

    private fun markAsTaken(target: ServerPlayerEntity, kitName: String) {
        (ModuleAPI.getModuleByName("basic") as ModuleObject).savePlayerData(target)
        userDataConfiguration.users.find {
            it.name == target.name.string || it.uuid == target.uniqueID.toString()
        }?.let { user ->
            user.lastKitsDates.map { value ->
                value.split('@').let { it[0] to it[1] }
            }.find { it.first == kitName }?.let { expiredKit ->
                user.lastKitsDates.removeAll { expiredKit.first in it }
            }
            user.lastKitsDates.add("$kitName@${ZonedDateTime.now()}")
        }
    }

    private fun permissionOf(kitName: String) = "ess.kit.receive.$kitName"
    private fun nullIfIllegalItem(item: String) = ITEMS.getValue(resource(item))?.item
    private fun nullIfIllegalEnchantment(value: String) = ENCHANTMENTS.getValue(resource(value))
    private fun fixItemCount(count: Int) = if (count < 1) 1 else if (count > 64) 64 else count
    private fun fixEnchantLevel(level: Int) = if (level < 1) 1 else level
    private fun resource(value: String) = ResourceLocation.read(StringReader(value))
}
