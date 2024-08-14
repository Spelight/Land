package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.skull
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common.util.sync
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.buildItem
import taboolib.platform.util.nextChat
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandCooperatorsMenu
 *
 * @author mical
 * @since 2024/8/3 16:45
 */
object LandCooperatorsMenu {

    fun openMenu(player: Player, land: Land, back: Consumer<Player>?, elements: List<Land>) {
        player.openMenu<PageableChest<OfflinePlayer>>("领地(ID:${land.id}) ${land.name} 合作者") {
            virtualize()

            map(
                "b===+==pn",
                "#########",
                "#########"
            )

            slotsBy('#')

            elements { land.cooperators.map { Bukkit.getOfflinePlayer(it) } }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) { LandInfoMenu.openMenu(player, land, back, elements) }

            onGenerate(async = true) { _, p, _, _ ->
                buildItem(XMaterial.PLAYER_HEAD) {
                    name = "&f" + p.name
                    lore += listOf(
                        "&e单击删除"
                    )
                    colored()
                }.skull(p.name)
            }

            onClick { _, p ->
                land.cooperators -= p.uniqueId
                openMenu(player, land, back, elements)
            }

            set('+', buildItem(XMaterial.NAME_TAG) {
                name = "&a添加合作者"
                lore += listOf(
                    "&7协作者具有领地的大部分权限",
                    "&7但没有领地的设置与修改权"
                )
                colored()
            }) {
                clicker.closeInventory()
                clicker.prettyInfo("请在聊天框输入合作者名字, 或输入'取消'来取消操作!")
                clicker.nextChat { ctx ->
                    if (ctx == "取消")
                        return@nextChat
                    val offlinePlayer = Bukkit.getOfflinePlayerIfCached(ctx)
                    if (offlinePlayer == null) {
                        clicker.prettyInfo("并没有找到这位玩家!")
                        return@nextChat
                    }
                    land.cooperators += offlinePlayer.uniqueId
                    clicker.prettyInfo("添加成功!")
                    sync { openMenu(clicker, land, back, elements) }
                }
            }

            onClose {
                land.export()
            }
        }
    }
}