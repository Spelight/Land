package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.display
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

/**
 * Realms
 * ink.ptms.realms.permission.Permission
 *
 * @author sky
 * @since 2021/3/18 9:20 上午
 */
interface Permission {

    /**
     * 界面优先级
     */
    val priority: Int
        get() = 0

    /**
     * 默认选项
     */
    val default: Boolean
        get() = true

    /**
     * 序号
     */
    val id: String

    /**
     * 世界权限
     */
    val worldSide: Boolean

    /**
     * 玩家权限
     */
    val playerSide: Boolean

    /**
     * 管理员可视
     */
    val adminSide: Boolean
        get() = false

    /**
     * 构建界面物品
     */
    fun generateMenuItem(land: Land, player: OfflinePlayer? = null): ItemStack

    /**
     * 适用于构建界面物品
     * Player == null -> 该领地的该权限节点的权限设置情况
     * Player != null -> 玩家在该领地的该权限节点的权限设置情况
     */
    fun flagValue(land: Land, user: OfflinePlayer? = null): String {
        if (user != null) {
            return land.users[user.uniqueId]?.get(id).display
        }
        return land.getFlagValueOrNull(id).display
    }
}