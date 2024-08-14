@file:Suppress("DEPRECATION")

package com.mcstarrysky.land.data

import com.mcstarrysky.land.flag.PermAdmin
import com.mcstarrysky.land.flag.PermTeleport
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.serializers.ChunkSerializer
import com.mcstarrysky.land.serializers.LocationSerializer
import com.mcstarrysky.land.serializers.UUIDSerializer
import com.mcstarrysky.land.util.DATE_FORMAT
import com.mcstarrysky.land.util.ZERO_UUID
import com.mcstarrysky.land.util.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common5.cbool
import java.util.Date
import java.util.HashMap
import java.util.UUID

/**
 * Land
 * com.mcstarrysky.land.data.Land
 *
 * @author mical
 * @since 2024/8/2 22:43
 */
@Serializable
data class Land(
    val id: Int,
    var name: String,
    @Serializable(with = UUIDSerializer::class)
    var owner: UUID,
    val timestamp: Long,
    val world: String,
    var description: String = "没有介绍",
    val area: MutableList<@Serializable(with = ChunkSerializer::class) Chunk>,
    var enterMessage: String? = "你进入了 &{#8abcd1}$name",
    var leaveMessage: String? = "你离开了 &{#8abcd1}$name",
    @Serializable(with = LocationSerializer::class)
    var tpLocation: Location,
    @Deprecated(message = "协作者功能已废除")
    val cooperators: MutableList<@Serializable(with = UUIDSerializer::class) UUID>,
    val flags: MutableMap<String, Boolean> = mutableMapOf(),
    // 玩家 UUID 对一个 Map, Map 是 权限节点对应的值
    val users: MutableMap<@Serializable(with = UUIDSerializer::class) UUID, MutableMap<String, Boolean>>,
) {

    init {
        // 迁移协作者
        if (cooperators.isNotEmpty()) {
            cooperators.forEach { uuid ->
                (users.computeIfAbsent(uuid) { HashMap() }) += PermAdmin.id to true
            }
        }
    }

    @Transient
    val date = DATE_FORMAT.format(Date(timestamp))

    fun isInArea(location: Location): Boolean {
        return area.any { it == location.chunk }
    }

    fun teleport(player: Player) {
        PermTeleport.teleport(player, this)
    }

    fun hasPermission(player: Player, perm: Permission? = null): Boolean {
        return if (perm == null) {
            player.isOp || player.uniqueId == owner || users[player.uniqueId]?.get(PermAdmin.id) == true
        } else {
            users[player.uniqueId]?.get(perm.id) ?: getFlag(perm.id)
        }
    }

    fun saveToString(): String {
        return json.encodeToString(this)
    }

    private fun getOwner(): OfflinePlayer? {
        if (owner != ZERO_UUID) {
            return Bukkit.getOfflinePlayer(owner)
        }
        return null
    }

    fun getOwnerName(): String {
        return getOwner()?.name ?: "StarrySky 管理组"
    }

    /**
     * 适用于权限判断时
     */
    fun getFlag(flag: String): Boolean {
        return flags[flag]?.cbool ?: LandManager.permissions.firstOrNull { it.id == flag }?.default ?: false
    }

    /**
     * 适用于 UI 展示
     */
    fun getFlagValueOrNull(flag: String): Boolean? {
        return flags[flag]?.cbool
    }

    /**
     * 设置权限
     * other == null -> 设置领地通用权限
     * other != null -> 设置玩家在该领地的权限
     */
    fun setFlag(flag: String, value: Boolean?, other: OfflinePlayer?) {
        if (value == null) {
            if (other != null) {
                (users.computeIfAbsent(other.uniqueId) { HashMap() }) -= flag
            } else {
                flags -= flag
            }
            return
        }
        if (other != null) {
            (users.computeIfAbsent(other.uniqueId) { HashMap() }) += flag to value
        } else {
            flags += flag to value
        }
    }

    fun export() {
        LandManager.save(this)
    }
}
