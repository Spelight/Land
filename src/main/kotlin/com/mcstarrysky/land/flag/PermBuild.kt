package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.display
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
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
 * com.mcstarrysky.land.flag.PermBuild
 *
 * @author mical
 * @since 2024/8/3 17:26
 */
object PermBuild : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "build"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land): ItemStack {
        return buildItem(XMaterial.GRASS_BLOCK) {
            name = "&f建筑 ${land.getFlagOrNull(id).display}"
            lore += listOf(
                "&7允许行为:",
                "&8放置方块, 破坏方块, 放置挂饰, 破坏挂饰",
                "&8放置盔甲架, 破坏盔甲架, 装满桶, 倒空桶",
                "",
                "&e左键修改值, 右键取消设置"
            )
            
            flags += ItemFlag.values().toList()
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player) && !getFlag(this@PermBuild.id)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止打破方块/挂画&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: BlockPlaceEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player) && !getFlag(this@PermBuild.id)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止放置方块/挂画或接触展示框&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: HangingPlaceEvent) {
        val player = e.player ?: return
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(player) && !getFlag(this@PermBuild.id)) {
                e.isCancelled = true
                player.prettyInfo("没有权限, 禁止放置方块/挂画或接触展示框&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: HangingBreakByEntityEvent) {
        if (e.remover is Player) {
            val player = e.remover as Player
            LandManager.getLand(e.entity.location.block.location)?.run {
                if (!hasPermission(player) && !getFlag(this@PermBuild.id)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止打破方块/挂画&7\\(标记: ${this@PermBuild.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_BLOCK && e.item?.type == org.bukkit.Material.ARMOR_STAND) {
            LandManager.getLand(e.clickedBlock?.location ?: return)?.run {
                if (!hasPermission(e.player) && !getFlag(this@PermBuild.id)) {
                    e.isCancelled = true
                    e.player.prettyInfo("没有权限, 禁止触碰盔甲架&7\\(标记: ${this@PermBuild.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.entity is ArmorStand) {
            val player = e.damager as? Player ?: return
//            val player = Servers.getAttackerInDamageEvent(e) ?: return
            LandManager.getLand(e.entity.location.block.location)?.run {
                if (!hasPermission(player) && !getFlag(this@PermBuild.id)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止触碰盔甲架&7\\(标记: ${this@PermBuild.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerBucketFillEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player) && !getFlag(this@PermBuild.id)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止水/岩浆桶倒/装&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerBucketEmptyEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player) && !getFlag(this@PermBuild.id)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止水/岩浆桶倒/装7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }
}