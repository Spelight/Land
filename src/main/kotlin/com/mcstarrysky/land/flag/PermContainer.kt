package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.display
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryOpenEvent
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
 * com.mcstarrysky.land.flag.PermContainer
 *
 * @author mical
 * @since 2024/8/3 17:47
 */
object PermContainer : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "container"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land): ItemStack {
        return buildItem(XMaterial.CHEST) {
            name = "&f容器 ${land.getFlagOrNull(id).display}"
            lore += listOf(
                "&7允许行为:",
                "&8打开容器",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        if (e.inventory.location != null) {
            val player = e.player as? Player ?: return
            LandManager.getLand(e.inventory.location ?: return)?.run {
                if (!hasPermission(player) && !getFlag(this@PermContainer.id)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止使用容器&7\\(标记: ${this@PermContainer.id}\\)")
                }
            }
        }
    }
}