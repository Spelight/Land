package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
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
        val title = if (other == null) "领地标记管理" else "${other.name} 的标记管理"
        player.openMenu<PageableChest<Permission>>(title) {
            // virtualize()

            map(
                "b======pn",
                "#########",
                "#########"
            )

            slotsBy('#')

            elements {
                LandManager.permissions.filter { if (other != null) it.playerSide else it.worldSide }.sortedBy { it.priority }.toMutableList()
                    .filter { if (player.isOp) true else it.adminSide == player.isOp }
            }

            onGenerate(async = true) { _, flag, _, _ ->
                if (flag.adminSide && !player.isOp) {
                    return@onGenerate ItemStack(Material.BARRIER)
                }
                return@onGenerate flag.generateMenuItem(land, other)
            }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) { back?.accept(player) }

            onClick { event, flag ->
                when (event.clickEvent().click) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        // FIXME: 这里的代码很乱, 仅仅是能用, 还需要后续解耦
                        // 如果是在设置全局权限
                        if (other == null) {
                            // 如果没设置, 就设置成默认值
                            if (land.getFlagValueOrNull(flag.id) == null) {
                                land.setFlag(flag.id, flag.default, null)
                            } else {
                                // 设置成相反的值
                                val value = land.getFlag(flag.id)
                                land.setFlag(flag.id, !value, null)
                            }
                        } else {
                            // 如果没设置, 就设置成默认值
                            if (land.getUserFlagValueOrNull(other, flag.id) == null) {
                                land.setFlag(flag.id, flag.default, other)
                            } else {
                                // 设置成相反的值
                                val value = land.getUserFlag(other, flag.id)
                                land.setFlag(flag.id, !value, other)
                            }
                        }

                        openMenu(player, land, other, back)
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        if (other == null) {
                            if (land.getFlagValueOrNull(flag.id) != null) {
                                land.setFlag(flag.id, null, null)
                            }
                        } else {
                            if (land.getUserFlagValueOrNull(other, flag.id) != null) {
                                land.setFlag(flag.id, null, other)
                            }
                        }
                        openMenu(player, land, other, back)
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