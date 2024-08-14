package com.mcstarrysky.land.listener

import com.mcstarrysky.land.Land
import com.mcstarrysky.land.manager.LandManager
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isLeftClickBlock
import taboolib.platform.util.isMainhand
import taboolib.platform.util.isRightClickBlock

/**
 * Land
 * com.mcstarrysky.land.listener.FreeLandHandler
 *
 * @author mical
 * @since 2024/8/3 14:35
 */
object FreeLandHandler {

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.isMainhand() && (e.isRightClickBlock() || e.isLeftClickBlock())) {
            if (e.player.equipment.itemInMainHand.isSimilar(Land.freeLandTool)) {
                LandManager.createFree(e.player, e.clickedBlock!!.location)
                e.isCancelled = true
            }
        }
    }
}