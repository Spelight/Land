package com.mcstarrysky.land.menu

import com.mcstarrysky.land.LandSettings
import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markBoard
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.cacheMessageWithPrefixColor
import com.mcstarrysky.land.util.isValidLandName
import com.mcstarrysky.land.util.prettyInfo
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem
import taboolib.platform.util.nextChat
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandInfoMenu
 *
 * @author mical
 * @since 2024/8/3 15:40
 */
object LandInfoMenu {

    fun openMenu(player: Player, land: Land, back: Consumer<Player>?, elements: List<Land>) {
        player.openMenu<Chest>("领地(ID:${land.id}) ${land.name}") {
            virtualize()

            map(
                "b========",
                " a z f d ", // 描述, 加入, 传送点, 标记
                " g c m e ", // 转让所有者, 退出, 合作者, 删除
                "========="
            )

            set('b', MenuRegistry.BACK) {
                LandListMenu.openMenu(player, elements, back)
            }

            markHeader()
            markBoard()

            set('a', buildItem(XMaterial.CHERRY_HANGING_SIGN) {
                name = "&f" + land.name
                lore += listOf(
                    "&7左键更改描述, 右键改名",
                    "&7",
                    "&{#dcc44c}世界: &7" + (LandSettings.worldAliases[land.world] ?: land.world),
                    "&{#dcc44c}描述: &7" + land.description,
                    "&{#dcc44c}所有者: &7" + land.getOwnerName(),
                    "&{#dcc44c}进入信息: &7" + cacheMessageWithPrefixColor(land.enterMessage ?: "无").toLegacyText(),
                    "&{#dcc44c}离开信息: &7" + cacheMessageWithPrefixColor(land.leaveMessage ?: "无").toLegacyText(),
                    "&{#dcc44c}创建时间: &7" + land.date
                )
                colored()
            }) {
                if (check(clicker, land)) {
                    when (virtualEvent().clickType) {
                        ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                            clicker.closeInventory()
                            clicker.prettyInfo("请在聊天框输入新的描述, 或输入'取消'来取消操作!")
                            clicker.nextChat { ctx ->
                                if (ctx == "取消")
                                    return@nextChat
                                land.description = ctx
                                land.export()
                                clicker.prettyInfo("修改成功!")
                            }
                        }
                        ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                            clicker.closeInventory()
                            clicker.prettyInfo("请在聊天框输入新的名字, 或输入'取消'来取消操作!")
                            clicker.nextChat { ctx ->
                                if (ctx == "取消")
                                    return@nextChat
                                if (!ctx.isValidLandName()) {
                                    clicker.prettyInfo("为避免与领地编号混淆, 名字不能是纯数字!")
                                    return@nextChat
                                }
                                if (LandManager.hasLand(ctx)) {
                                    clicker.prettyInfo("你输入的新名字已存在! 请重新操作")
                                    return@nextChat
                                }
                                land.name = ctx
                                land.export()
                                clicker.prettyInfo("修改成功!")
                            }
                        }
                        else -> {
                        }
                    }
                }
            }

            set('z', buildItem(XMaterial.CHERRY_DOOR) {
                name = "&e设置进入信息"
                lore += listOf(
                    "&7注意,不管将进入信息修改成什么",
                    "&7都会带一个领地前缀",
                    "&7",
                    "&e单击修改,右键关闭进入信息"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                when (virtualEvent().clickType) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        clicker.closeInventory()
                        clicker.prettyInfo("请在聊天框输入新的进入信息, 支持行内复合文本. 输入'取消'来取消操作!")
                        clicker.nextChat { ctx ->
                            if (ctx == "取消")
                                return@nextChat
                            land.enterMessage = ctx
                            land.export()
                            clicker.prettyInfo("修改成功!")
                        }
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        clicker.closeInventory()
                        land.enterMessage = null
                        land.export()
                        clicker.prettyInfo("修改成功!")
                    }
                    else -> {
                    }
                }
            }

            set('c', buildItem(XMaterial.IRON_DOOR) {
                name = "&e设置离开信息"
                lore += listOf(
                    "&7注意,不管将离开信息修改成什么",
                    "&7都会带一个领地前缀",
                    "&7",
                    "&e单击修改,右键关闭离开信息"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                when (virtualEvent().clickType) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        clicker.closeInventory()
                        clicker.prettyInfo("请在聊天框输入新的离开信息, 支持行内复合文本. 输入'取消'来取消操作!")
                        clicker.nextChat { ctx ->
                            if (ctx == "取消")
                                return@nextChat
                            land.leaveMessage = ctx
                            land.export()
                            clicker.prettyInfo("修改成功!")
                        }
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        clicker.closeInventory()
                        land.leaveMessage = null
                        land.export()
                        clicker.prettyInfo("修改成功!")
                    }
                    else -> {
                    }
                }
            }

            set('f', buildItem(XMaterial.COMPASS) {
                name = "&b设置传送点"
                lore += listOf(
                    "&7点击设置你脚下为领地传送点"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                if (!land.isInArea(clicker.location)) {
                    clicker.prettyInfo("你必须设置一个领地内的点!")
                    return@set
                }
                land.tpLocation = clicker.location
                land.export()
                clicker.prettyInfo("修改成功!")
            }

            set('g', buildItem(XMaterial.ENDER_EYE) {
                name = "&c转让所有者"
                lore += listOf(
                    "&7注意: 此操作不可撤销!"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                clicker.closeInventory()
                clicker.prettyInfo("请在聊天框输入新的玩家名, 或输入'取消'来取消操作!")
                clicker.nextChat { ctx ->
                    if (ctx == "取消")
                        return@nextChat
                    val offlinePlayer = Bukkit.getOfflinePlayerIfCached(ctx)
                    if (offlinePlayer == null) {
                        clicker.prettyInfo("并没有找到这位玩家!")
                        return@nextChat
                    }
                    land.owner = offlinePlayer.uniqueId
                    land.export()
                    clicker.prettyInfo("转让成功!")
                }
            }

            set('e', buildItem(XMaterial.BARRIER) {
                name = "&c删除领地"
                lore += listOf(
                    "&7注意: 此操作不可撤销!"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                clicker.closeInventory()
                clicker.prettyInfo("你确定要删除吗? 输入'确认'来确认, 或输入其他内容来取消操作!")
                clicker.nextChat { ctx ->
                    if (ctx != "确认")
                        return@nextChat
                    LandManager.lands -= land
                    clicker.prettyInfo("已删除领地!")
                }
            }

            set('d', buildItem(XMaterial.NAME_TAG) {
                name = "&e查看领地标记"
                lore += listOf(
                    "&7设置各个权限"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                LandFlagsMenu.openMenu(player, land, null) {
                    openMenu(player, land, back, elements)
                }
            }

            set('m', buildItem(XMaterial.PLAYER_HEAD) {
                name = "&a管理玩家权限"
                lore += listOf(
                    "&7管理指定玩家在领地内的权限",
                    "&7例如设置某个玩家禁止移动"
                )
                colored()
            }) {
                if (!check(player, land)) return@set
                LandPlayerPermsMenu.openMenu(player, land) {
                    openMenu(player, land, back, elements)
                }
            }
        }
    }

    private fun check(player: Player, land: Land): Boolean {
        if (!land.hasPermission(player)) {
            player.closeInventory()
            player.prettyInfo("你没有权限修改这个领地的内容!")
            return false
        }
        return true
    }
}