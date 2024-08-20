package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermFishingRod
 *
 * @author mical
 * @date 2024/8/20 20:21
 */
object PermFishingRod : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "fishing_rod"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.CARROT_ON_A_STICK) {
            name = "&f钓鱼竿 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8使用钓鱼竿钓生物 / 钓鱼",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerFishEvent) {
        // TODO: 鱼竿触发压力板(可以归结为任何压力板的使用, 比如踩上去), 禁止使用按钮/红石
        // 钓生物
        if (e.state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            val fisher = e.player
            val whoCaught = e.caught ?: return

            LandManager.getLand(whoCaught.location)?.run {
                if (!hasPermission(fisher, this@PermFishingRod)) {
                    // 无权使用钓竿钓实体
                    fisher.prettyInfo("没有权限, 禁止使用钓竿钩实体&7\\(标记: ${this@PermFishingRod.id}\\)")
                    e.isCancelled = true
                    return
                }
            }
        }
    }
}