package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermIgnite
 *
 * @author HXS__
 * @since 2024/8/26 0:43
 */
object PermIgnite : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "ignite"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.FLINT_AND_STEEL) {
            name = "&f火焰蔓延 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8火焰蔓延",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: BlockIgniteEvent) {
        if (e.cause == BlockIgniteEvent.IgniteCause.SPREAD) {
            LandManager.getLand(e.block.location)?.run {
                if (!getFlag(this@PermIgnite.id)) {
                    e.isCancelled = true
                }
            }
        }
    }
}