package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.TileState
import org.bukkit.event.player.PlayerInteractEvent
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
        Material.CRAFTING_TABLE, // 工作台
        Material.STONECUTTER, // 切石机
        Material.CARTOGRAPHY_TABLE, // 制图台
        Material.SMITHING_TABLE, // 锻造台
        Material.GRINDSTONE, // 砂轮
        Material.LOOM, // 织布机
        Material.FURNACE, // 熔炉
        Material.BLAST_FURNACE, // 高炉
        Material.SMOKER, // 烟熏炉
        Material.BREWING_STAND, // 酿造台
        Material.ENCHANTING_TABLE, // 附魔台
        Material.ANVIL, // 铁砧
        Material.CHIPPED_ANVIL,// 开裂的铁砧
        Material.DAMAGED_ANVIL// 损坏的铁砧
    )

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractEvent) {
        if (e.isRightClickBlock()) {
            val block = e.clickedBlock ?: return
            // 工作方块权限跳过
            if (functionalBlocks.contains(block.type)) {
                return
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