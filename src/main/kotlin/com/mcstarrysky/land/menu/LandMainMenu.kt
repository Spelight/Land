package com.mcstarrysky.land.menu

import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.prettyInfo
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import taboolib.common.util.sync
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem
import taboolib.platform.util.nextChat
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandMainMenu
 *
 * @author mical
 * @since 2024/8/2 23:16
 */
object LandMainMenu {

    fun openMenu(player: Player, back: Consumer<Player>? = null) {
        player.openMenu<Chest>("领地主菜单") {
            virtualize()

            map(
                "b========",
                "m n c d e", // 我的领地 全服领地 脚下领地 领地选择
                "========="
            )

            markHeader()

            if (back != null) {
                set('b', MenuRegistry.BACK) {
                    back.accept(player)
                }
            } else {
                set('b', MenuRegistry.HEAD)
            }

            set('m', buildItem(XMaterial.NETHER_STAR) {
                name = "&{#5cb3cc}查看我的领地列表"
                colored()
            }) {
                LandListMenu.openMenu(
                    player,
                    LandManager.getLands(player),
                    back
                )
            }

            set('n', buildItem(XMaterial.ENDER_PEARL) {
                name = "&{#66c18c}查看全部领地列表"
                colored()
            }) {
                LandListMenu.openMenu(
                    player,
                    LandManager.lands,
                    back
                )
            }

            set('c', buildItem(XMaterial.NETHERITE_BOOTS) {
                name = "&{#fcd337}查看当前位置的领地"
                colored()
                flags += ItemFlag.values().toList()
            }) {
                LandListMenu.openMenu(
                    player,
                    listOfNotNull(LandManager.getLand(player.location)),
                    back
                )
            }

            set('d', buildItem(XMaterial.NAME_TAG) {
                name = "&e搜索领地"
                colored()
            }) {
                clicker.closeInventory()
                clicker.closeInventory()
                clicker.prettyInfo("请在聊天框输入领地编号或名字包含的字词, 或输入'取消'来取消操作!")
                clicker.nextChat { ctx ->
                    if (ctx == "取消")
                        return@nextChat
                    sync {
                        LandListMenu.openMenu(
                            player,
                            LandManager.lands.filter { it.id.toString() == ctx || it.name.contains(ctx) },
                            back
                        )
                    }
                }
            }

            set('e', buildItem(XMaterial.MAP) {
                name = "&{#f97d1c}选择范围菜单"
                colored()
            }) {
                LandSelectionMenu.openMenu(clicker, back)
            }
        }
    }
}