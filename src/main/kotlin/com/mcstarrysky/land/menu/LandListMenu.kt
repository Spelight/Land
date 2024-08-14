package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.buildItem
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandListMenu
 *
 * @author mical
 * @since 2024/8/2 23:34
 */
object LandListMenu {

    fun openMenu(player: Player, elements: List<Land>, back: Consumer<Player>?) {
        player.openMenu<PageableChest<Land>>("领地列表 #%p") {
            // virtualize()

            map(
                "b======pn",
                "#########",
                "#########",
                "#########"
            )

            slotsBy('#')

            elements { elements }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) {
                LandMainMenu.openMenu(player, back)
            }

            onGenerate(async = true) { _, land, _, _ ->
                buildItem(XMaterial.MAP) {
                    name = "&f" + land.name
                    lore += listOf(
                        "&7左键传送, 右键查看",
                        "&7",
                        "&{#dcc44c}编号: &7" + land.id,
                        "&{#dcc44c}描述: &7" + land.description
                    )
                    colored()
                }
            }

            onClick { e, land ->
                when (e.clickEvent().click) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        e.clicker.closeInventory()
                        land.teleport(e.clicker)
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        LandInfoMenu.openMenu(player, land, back, elements)
                    }
                    else -> {
                    }
                }
            }
        }
    }
}