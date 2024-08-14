package com.mcstarrysky.land.listener

import com.mcstarrysky.land.Land
import org.bukkit.Chunk
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isLeftClickBlock
import taboolib.platform.util.isMainhand
import taboolib.platform.util.isRightClickBlock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Land
 * com.mcstarrysky.land.listener.PosSelection
 *
 * @author mical
 * @since 2024/8/2 23:08
 */
object PosSelection {

    val pos1 = ConcurrentHashMap<UUID, Chunk>()
    val pos2 = ConcurrentHashMap<UUID, Chunk>()

    // @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        val player = e.player
        // 主手
        if (e.isMainhand()) {
            // 是选择棒
            if (player.equipment.itemInMainHand.isSimilar(Land.tool)) {
                // 判断左右键
                when {
                    e.isLeftClickBlock() -> pos1.computeIfAbsent(player.uniqueId) { e.clickedBlock!!.location.chunk }
                    e.isRightClickBlock() -> pos2.computeIfAbsent(player.uniqueId) { e.clickedBlock!!.location.chunk }
                }
            }
        }
    }
}