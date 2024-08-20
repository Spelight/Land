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
object PermFishing : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "fishing"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.TROPICAL_FISH) {
            name = "&f钓鱼 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8钓鱼",
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
        // 钓鱼 || 鱼跑了 || 咬钩
        if (e.state == PlayerFishEvent.State.CAUGHT_FISH ||
            e.state == PlayerFishEvent.State.FAILED_ATTEMPT ||
            e.state == PlayerFishEvent.State.BITE
        ) {
            val fisher = e.player

            LandManager.getLand(e.hook.location)?.run {
                if (!hasPermission(fisher, this@PermFishing)) {
                    // 无权使用钓竿钓实体
                    fisher.prettyInfo("没有权限, 禁止钓鱼&7\\(标记: ${this@PermFishing.id}\\)")
                    e.isCancelled = true
                    return
                }
            }
        }
    }
}