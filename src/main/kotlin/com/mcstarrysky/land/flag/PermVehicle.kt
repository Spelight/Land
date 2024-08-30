package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.isRightClickBlock

/**
 * Land
 * com.mcstarrysky.land.flag.PermVehicle
 *
 * @author HXS__
 * @since 2024/8/26 0:54
 */
object PermVehicle : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "vehicle"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.OAK_BOAT) {
            name = "&f交通工具 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8放置/使用/使用交通工具",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    // 交通工具的list
    private val vehicle = listOf(
        Material.BIRCH_BOAT,
        Material.OAK_BOAT,
        Material.ACACIA_BOAT,
        Material.SPRUCE_BOAT,
        Material.JUNGLE_BOAT,
        Material.DARK_OAK_BOAT,
        Material.MANGROVE_BOAT,
        Material.CHERRY_BOAT,
        Material.MINECART,
    )

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractEvent) {
        if (e.isRightClickBlock()) {
            val itemInHand = e.player.inventory.itemInMainHand
            if (vehicle.contains(itemInHand.type)) {
                LandManager.getLand(e.clickedBlock?.location ?: return)?.run {
                    if (!hasPermission(e.player, this@PermVehicle)) {
                        e.isCancelled = true
                        e.player.prettyInfo("没有权限, 禁止放置交通工具&7\\(标记: ${this@PermVehicle.id}\\)")
                    }
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: VehicleDestroyEvent) {
        if (e.attacker is Player) {
            val player = e.attacker as Player
            LandManager.getLand(e.vehicle.location)?.run {
                if (!hasPermission(player, this@PermVehicle)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止破坏交通工具&7\\(标记: ${this@PermVehicle.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: VehicleEnterEvent) {
        if (e.entered is Player) {
            val player = e.entered as Player
            LandManager.getLand(player.location)?.run {
                if (!hasPermission(player, this@PermVehicle)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止乘坐交通工具&7\\(标记: ${this@PermVehicle.id}\\)")
                }
            }
        }
    }
}
