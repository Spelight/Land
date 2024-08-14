package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.display
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermTeleport
 *
 * @author mical
 * @since 2024/8/3 00:59
 */
object PermTeleport : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "teleport"

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land): ItemStack {
        return buildItem(XMaterial.ENDER_PEARL) {
            name = "&f传送 ${land.getFlagOrNull(id).display}"
            lore += listOf(
                "&7允许行为:",
                "&8传送到该领地",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

    fun teleport(player: Player, land: Land) {
        if (land.getFlag(id) || land.hasPermission(player)) {
            player.teleportAsync(land.tpLocation)
            player.prettyInfo("传送完成!")
        } else {
            player.prettyInfo("没有权限, 禁止使用传送点&7\\(标记: ${id}\\)")
        }
    }
}