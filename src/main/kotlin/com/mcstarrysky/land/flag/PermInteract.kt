package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.block.TileState
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.isRightClickBlock

/**
 * Land
 * com.mcstarrysky.land.flag.PermInteract
 *
 * @author mical
 * @since 2024/8/3 17:43
 */
object PermInteract : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "interact"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.OAK_DOOR) {
            name = "&f交互 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8方块交互",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    private val functionalBlocks  = listOf(
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
    fun e(e: PlayerInteractEvent) {
        if (e.isRightClickBlock()) {
            val block = e.clickedBlock ?: return
            // 工作方块权限跳过
            if (block.state is InventoryHolder) {
                val inventoryHolder = block.state as? InventoryHolder ?: return
                if (functionalBlocks.contains(inventoryHolder.inventory.type)) {
                    return
                }
            }
            val itemInHand = e.player.inventory.itemInMainHand
            // 在方块不可交互 & 饱食度未满 & 手中物品为食物 跳过
            if (block.state !is TileState && e.player.foodLevel < 20 && itemInHand.type.isEdible){
                return
            }
            LandManager.getLand(e.clickedBlock?.location ?: return)?.run {
                if (!hasPermission(e.player, this@PermInteract)) {
                    e.isCancelled = true
                    e.player.prettyInfo("没有权限, 禁止接触任意物品方块&7\\(标记: ${this@PermInteract.id}\\)")
                }
            }
        }
    }
}