package com.mcstarrysky.land.data

import com.mcstarrysky.land.flag.PermTeleport
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
    val cooperators: MutableList<@Serializable(with = UUIDSerializer::class) UUID>,
    val flags: MutableMap<String, Boolean> = mutableMapOf()
) {

    @Transient
    val date = DATE_FORMAT.format(Date(timestamp))

    fun isInArea(location: Location): Boolean {
        return area.any { it == location.chunk }
    }

    fun teleport(player: Player) {
        PermTeleport.teleport(player, this)
    }

    fun hasPermission(player: Player): Boolean {
        return player.isOp || player.uniqueId == owner || player.uniqueId in cooperators
    }

    fun saveToString(): String {
        return json.encodeToString(this)
    }

    fun getOwner(): OfflinePlayer? {
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
    fun getFlagOrNull(flag: String): Boolean? {
        return flags[flag]?.cbool
    }

    fun setFlag(flag: String, value: Boolean?) {
        if (value == null) {
            flags -= flag
            return
        }
        flags += flag to value
    }

    fun export() {
        LandManager.save(this)
    }
}
