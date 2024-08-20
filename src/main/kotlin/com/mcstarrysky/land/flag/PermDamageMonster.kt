package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
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
 * com.mcstarrysky.land.flag.PermDamageMonster
 *
 * @author HXS
 * @since 2024/8/14 14:49
 */
object PermDamageMonster : Permission{

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "damage_monster"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

        override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
            return buildItem(XMaterial.GOLDEN_SWORD){
                name = "&f攻击怪物 ${flagValue(land, player)}"
                lore += listOf(
                "&7允许行为:",
                "&8对怪物造成伤害",
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
        if (e.entity is Monster) {
            val player = e.attacker as? Player ?: return
            LandManager.getLand(e.entity.location)?.run {
                if (!hasPermission(player, this@PermDamageMonster)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止攻击怪物&7\\(标记: ${this@PermDamageMonster.id}\\)")
                }
            }
        }
    }
}