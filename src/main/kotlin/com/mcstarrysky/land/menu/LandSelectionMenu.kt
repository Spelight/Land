package com.mcstarrysky.land.menu

import com.mcstarrysky.land.Land
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markBoard
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.prettyInfo
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandSelectionMenu
 *
 * @author mical
 * @since 2024/8/3 17:00
 */
object LandSelectionMenu {

    fun openMenu(player: Player, back: Consumer<Player>?) {
        player.openMenu<Chest>("选择范围") {
            virtualize()

            map(
                "b========",
                "    f    ", // 免费领地棒
                "========="
            )

            markHeader()

            set('b', MenuRegistry.BACK) { LandMainMenu.openMenu(player, back) }

            markBoard()

            set('f', buildItem(XMaterial.ARROW) {
                name = "&b免费领地棒"
                lore += listOf(
                    "&e单击领取",
                    "&7",
                    "&7点击方块即可免费创建并获取一个领地",
                    "&7领地大小为5*5区块",
                    "&7领地中心为你点击的方块的位置",
                    "&7只能在&{#8abcd1}主世界&7使用,且只能使用一次",
                    "&7领地大小获取后可自行扩展"
                )
                colored()
            }) {
                clicker.closeInventory()
                clicker.giveItem(Land.freeLandTool.clone())
                clicker.prettyInfo("获取免费领地棒成功.")
            }
        }
    }
}