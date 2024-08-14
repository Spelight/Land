package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermExplosion
 *
 * @author mical
 * @since 2024/8/3 14:26
 */
object PermEntityExplosion : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "entity_explosion"

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: Player?): ItemStack {
        return buildItem(XMaterial.CREEPER_SPAWN_EGG) {
            name = "&f爆炸 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8生物爆炸",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(EventPriority.MONITOR)
    fun e(e: EntityExplodeEvent) {
        e.blockList().removeIf {
            val land = LandManager.getLand(it.location)
            land != null && !land.getFlag(id)
        }
    }
}