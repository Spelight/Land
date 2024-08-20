package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.attacker
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermPVP
 *
 * @author HXS
 * @since 2024/8/15 18:49
 */
object PermPVP : Permission{

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "pvp"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

        override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
            return buildItem(XMaterial.DIAMOND_SWORD){
                name = "&f攻击玩家 ${flagValue(land, player)}"
                lore += listOf(
                "&7允许行为:",
                "&8玩家之间PVP",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val player = e.attacker as? Player ?: return
            LandManager.getLand(e.entity.location)?.run {
                if (!hasPermission(player, this@PermPVP)) {
                    e.isCancelled = true
                    player.prettyInfo("禁止PVP&7\\(标记: ${this@PermPVP.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.CAUGHT_ENTITY && e.caught is Player) {
            val player = e.player // 钓鱼者
            val caughtPlayer = e.caught as? Player ?: return // 被钓到的玩家

            LandManager.getLand(caughtPlayer.location)?.run {
                if (!hasPermission(caughtPlayer, this@PermPVP)) {
                    e.isCancelled = true
                    caughtPlayer.prettyInfo("禁止使用钓鱼竿PVP&7\\(标记: ${this@PermPVP.id}\\)")
                    return
                }
            }

            LandManager.getLand(player.location)?.run {
                if (!hasPermission(player, this@PermPVP)) {
                    e.isCancelled = true
                    player.prettyInfo("禁止使用钓鱼竿PVP&7\\(标记: ${this@PermPVP.id}\\)")
                    return
                }
            }
        }
    }
}