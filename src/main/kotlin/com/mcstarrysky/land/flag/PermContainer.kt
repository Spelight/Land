package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
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

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.CHEST) {
            name = "&f容器 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8打开容器",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    private val functionalBlocks = listOf(
        InventoryType.WORKBENCH, // 工作台
        InventoryType.STONECUTTER, // 切石机
        InventoryType.CARTOGRAPHY, // 制图台
        InventoryType.SMITHING, // 锻造台
        InventoryType.GRINDSTONE, // 砂轮
        InventoryType.LOOM, // 织布机
        InventoryType.FURNACE, // 熔炉
        InventoryType.BLAST_FURNACE, // 高炉
        InventoryType.SMOKER, // 烟熏炉
        InventoryType.BREWING, // 酿造台
        InventoryType.ENCHANTING, // 附魔台
        InventoryType.ANVIL // 铁砧
    )

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        if (e.inventory.location != null && !functionalBlocks.contains(e.inventory.type)) {
            val player = e.player as? Player ?: return
            LandManager.getLand(e.inventory.location ?: return)?.run {
                if (!hasPermission(player, this@PermContainer)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止使用容器&7\\(标记: ${this@PermContainer.id}\\)")
                }
            }
        }
    }
}