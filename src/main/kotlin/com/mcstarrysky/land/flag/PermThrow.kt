package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermThrow
 *
 * @author HXS
 * @since 2024/8/14 16:51
 */
object PermThrow : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "throw"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.EGG) {
            name = "&f抛射物 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8抛射鸡蛋, 抛射雪球, 扔掷三叉戟",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractEvent) {
        val item = e.item ?: return
        if (item.type == Material.SNOWBALL || item.type == Material.EGG || item.type == Material.TRIDENT) {
            LandManager.getLand(e.player.location)?.run {
                if (!hasPermission(e.player, this@PermThrow)) {
                    e.isCancelled = true
                    e.player.prettyInfo("没有权限, 禁止攻击投掷&7\\(标记: ${this@PermThrow.id}\\)")
                }
            }
        }
    }
}