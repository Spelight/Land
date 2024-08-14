package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta

/**
 * Land
 * com.mcstarrysky.land.flag.PermTeleportIn
 *
 * @author mical
 * @since 2024/8/3 21:09
 */
object PermTeleportIn : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "teleport_in"

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: Player?): ItemStack {
        return buildItem(XMaterial.DIAMOND_BOOTS) {
            name = "&f传送进来 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8传送进来",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerTeleportEvent) {
        LandManager.getLand(e.to)?.run {
            if (LandManager.getLand(e.from)?.id != id) {
                if (!hasPermission(e.player, this@PermTeleportIn)) {
                    if (e.player.hasMeta("land_ban_move_teleport")) {
                        e.player.removeMeta("land_ban_move_teleport")
                    } else {
                        e.isCancelled = true
                        e.player.prettyInfo("没有权限, 禁止传送进来&7\\(标记: ${this@PermTeleportIn.id}\\)")
                    }
                }
            }
        }
    }
}