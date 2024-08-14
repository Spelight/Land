package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Villager
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermTrade
 *
 * @author HXS
 * @since 2024/8/14 14:57
 */
object PermTrade : Permission{

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "trade"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: Player?): ItemStack {
        return buildItem(XMaterial.EMERALD)
            name += "&f交易 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8村民交易",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractAtEntityEvent) {
        if (e.rightClicked is Villager) {
            LandManager.getLand(e.rightClicked?.location ?: return)?.run {
                if (!hasPermission(e.player, this@PermTrade)) {
                    e.isCancelled = true
                    e.player.prettyInfo("没有权限, 禁止村民交易&7\\(标记: ${this@PermTrade.id}\\)")
                }
            }
        }
    }
}