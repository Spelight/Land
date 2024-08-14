package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandFlagsMenu
 *
 * @author mical
 * @since 2024/8/3 16:27
 */
object LandFlagsMenu {

    fun openMenu(player: Player, land: Land, other: OfflinePlayer?, back: Consumer<Player>?) {
        player.openMenu<PageableChest<Permission>>("标记管理") {
            virtualize()

            map(
                "b======pn",
                "#########",
                "#########"
            )

            slotsBy('#')

            // TODO: filter
            elements { LandManager.permissions }

            onGenerate(async = true) { _, flag, _, _ -> flag.generateMenuItem(land, other) }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) { back?.accept(player) }

            onClick { event, flag ->
                when (event.virtualEvent().clickType) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        // 如果没设置, 就设置成默认值
                        if (land.getFlagValueOrNull(flag.id) == null) {
                            land.setFlag(flag.id, flag.default, other)
                        } else {
                            val value = land.getFlag(flag.id)
                            land.setFlag(flag.id, !value, other)
                        }
                        openMenu(player, land, other, back)
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        if (land.getFlagValueOrNull(flag.id) != null) {
                            land.setFlag(flag.id, null, other)
                            openMenu(player, land, other, back)
                        }
                    }
                    else -> {
                    }
                }
            }

            onClose {
                land.export()
            }
        }
    }
}