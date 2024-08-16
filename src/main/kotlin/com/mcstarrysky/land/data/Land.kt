package com.mcstarrysky.land.data

import com.mcstarrysky.land.Land
import com.mcstarrysky.land.flag.PermAdmin
import com.mcstarrysky.land.flag.PermTeleport
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.serializers.LocationSerializer
import com.mcstarrysky.land.serializers.UUIDSerializer
import com.mcstarrysky.land.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.cbool
import taboolib.module.chat.Components
import taboolib.platform.util.checkItem
import taboolib.platform.util.takeItem
import java.util.Date
import java.util.UUID
import kotlin.collections.HashMap

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
//     val area: MutableList<@Serializable(with = ChunkSerializer::class) Chunk>,
    val area: MutableList<LandChunk>,
    var enterMessage: String? = "你进入了 &{#8abcd1}$name",
    var leaveMessage: String? = "你离开了 &{#8abcd1}$name",
    @Serializable(with = LocationSerializer::class)
    var tpLocation: Location,
//    @Deprecated(message = "协作者功能已废除")
//    val cooperators: MutableList<@Serializable(with = UUIDSerializer::class) UUID>,
    val flags: MutableMap<String, Boolean> = mutableMapOf(),
    // 玩家 UUID 对一个 Map, Map 是 权限节点对应的值
    val users: MutableMap<@Serializable(with = UUIDSerializer::class) UUID, MutableMap<String, Boolean>>,
) {

//    init {
//        // 迁移协作者
//        if (cooperators.isNotEmpty()) {
//            cooperators.forEach { uuid ->
//                (users.computeIfAbsent(uuid) { HashMap() }) += PermAdmin.id to true
//            }
//            cooperators.clear()
//        }
//    }

    @Transient
    val date = DATE_FORMAT.format(Date(timestamp))

    fun isInArea(location: Location): Boolean {
        return area.any { it.isEqualBkChunk(location.chunk) }
    }

    fun tryClaim(player: Player) {
        val location = player.location
        if (isInArea(location)) {
            player.prettyInfo("你已经占领了这个区块了!")
            return
        }
        if (LandManager.lands.any { it.id != id && it.isInArea(location) }) {
            player.prettyInfo("你所要占领的区块已被其他领地占领, 请换一个区块!")
            return
        }
        if (!ChunkUtils.isAdjacentToAnyChunk(location.chunk, area)) {
            player.prettyInfo("占领的区块必须与你领地相邻!")
            return
        }
        if (player.checkItem(Land.crystal, 3)) {
            player.inventory.takeItem(3) { it.isSimilar(Land.crystal) }
            area += location.chunk.toLandChunk()
            player.prettyInfo("占领区块成功!")
        } else {
            // 这里用到一个奇怪的操作
            Components.parseRaw(
                GsonComponentSerializer.gson()
                    .serialize(GsonComponentSerializer.gson().deserialize(cacheMessageWithPrefix("抱歉, 你要准备 3 个 ").toRawMessage())
                        .append(LegacyComponentSerializer.legacyAmpersand().deserialize("&b开拓水晶")
                            .hoverEvent(Land.crystal.clone().asHoverEvent())))
            ).append(cacheMessageWithPrefixColor(" 才能占领一个区块"))
                .sendTo(adaptPlayer(player))
        }
    }

    fun teleport(player: Player) {
        PermTeleport.teleport(player, this)
    }

    fun isAdmin(player: Player): Boolean {
        // 判断 Op 为了我可以神权
        return player.isOp || owner == player.uniqueId
    }

    fun hasAdminPerm(player: Player): Boolean {
        return users[player.uniqueId]?.get(PermAdmin.id) == true
    }

    fun hasPermission(player: Player, perm: Permission): Boolean {
        return isAdmin(player) || hasAdminPerm(player) || users[player.uniqueId]?.get(perm.id) ?: getFlag(perm.id)
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

    fun getUserFlag(user: OfflinePlayer, flag: String): Boolean {
        return users[user.uniqueId]?.get(flag) ?: LandManager.permissions.firstOrNull { it.id == flag }?.default ?: false
    }

    fun getUserFlagValueOrNull(user: OfflinePlayer, flag: String): Boolean? {
        return users[user.uniqueId]?.get(flag)
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
