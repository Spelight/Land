package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.display
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Golem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermDamageGolem
 *
 * @author HXS
 * @since 2024/8/14 13:57
 */
object PermDamageGolem : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "damage_golem"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land): ItemStack {
        return buildItem(XMaterial.STONE_SWORD){
            name = "&f攻击傀儡 ${land.getFlagOrNull(id).display}"
            lore += listOf(
                "&7允许行为:",
                "&8对傀儡 (Golem) 造成伤害",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.entity is Golem) {
            val player = e.damager as? Player ?: return
            LandManager.getLand(e.entity.location)?.run {
                if (!hasPermission(player) && !getFlag(this@PermDamageGolem.id)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止攻击傀儡&7\\(标记: ${this@PermDamageGolem.id}\\)")
                }
            }
        }
    }
}