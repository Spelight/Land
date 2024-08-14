package com.mcstarrysky.land.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.util.MenuRegistry
 *
 * @author mical
 * @since 2024/8/2 23:20
 */
object MenuRegistry {

    val NEXT: ItemStack = buildItem(Material.ARROW) {
        name = "&f下一页"
        colored()
    }

    val PRE: ItemStack = buildItem(Material.ARROW) {
        name = "&f上一页"
        colored()
    }

    val NO_NEXT: ItemStack = buildItem(Material.FEATHER) {
        name = "&f没有下一页"
        colored()
    }

    val NO_PRE: ItemStack = buildItem(Material.FEATHER) {
        name = "&f没有上一页"
        colored()
    }

    val HEAD = buildItem(Material.BLACK_STAINED_GLASS_PANE) {
        name = " "
    }

    val BOARD = buildItem(Material.GRAY_STAINED_GLASS_PANE) {
        name = " "
    }

    val CLOSE = buildItem(Material.BARRIER) {
        name = "&c关闭菜单"
        colored()
    }

    val BACK = buildItem(Material.CLOCK) {
        name = "&{#ddca57}返回上一菜单"
        colored()
    }

    fun Chest.markHeader(
        char: Char = '=',
        itemStack: ItemStack = HEAD
    ) {
        set(char, itemStack)
        onClick(char) { event ->
            event.isCancelled = true
        }
    }

    fun Chest.markBoard(
        char: Char = ' ',
        itemStack: ItemStack = BOARD
    ) {
        set(char, itemStack)
        onClick(char) { event ->
            event.isCancelled = true
        }
    }

    fun <T> PageableChest<T>.markPageButton(
        next: Char = 'n',
        previous: Char = 'p'
    ) {
        setNextPage(getFirstSlot(next)) { _, hasNextPage ->
            if (hasNextPage) {
                NEXT
            } else {
                NO_NEXT
            }
        }
        setPreviousPage(getFirstSlot(previous)) { _, hasNextPage ->
            if (hasNextPage) {
                PRE
            } else {
                NO_PRE
            }
        }
    }
}