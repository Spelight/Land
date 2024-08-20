package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermItem
 *
 * @author HXS
 * @since 2024/8/14 16:43
 */
object PermItem : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "item"

    override val default: Boolean
        get() = true

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.APPLE) {
            name = "&f物品 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8物品丢弃, 物品捡起",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        LandManager.getLand(e.player.location)?.run {
            if (!hasPermission(e.player, this@PermItem)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止丢弃物品&7\\(标记: ${this@PermItem.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: EntityPickupItemEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            LandManager.getLand(e.entity.location)?.run {
                if (!hasPermission(player, this@PermItem)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止捡起物品&7\\(标记: ${this@PermItem.id}\\)")
                }
            }
        }
    }
}