package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermAdmin
 *
 * @author mical
 * @since 2024/8/14 11:58
 */
object PermAdmin : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "admin"

    override val priority: Int
        get() = -1

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = false

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.COMMAND_BLOCK) {
            name = "&f管理权力 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8除管理领地与扩展领地外的所有权力",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }
}