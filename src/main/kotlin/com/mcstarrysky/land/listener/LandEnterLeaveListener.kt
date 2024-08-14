package com.mcstarrysky.land.listener

import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import org.bukkit.event.player.PlayerMoveEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * Land
 * com.mcstarrysky.land.listener.KandEnterLeaveListener
 *
 * @author mical
 * @since 2024/8/3 14:48
 */
object LandEnterLeaveListener {

    // @SubscribeEvent
    fun e(e: PlayerMoveEvent) {
        if (e.from.x != e.to.x || e.from.y != e.to.y || e.from.z != e.to.z) {
            val from = LandManager.getLand(e.from)
            val to = LandManager.getLand(e.to)
            if (from != to) {
                from?.leaveMessage?.let { e.player.prettyInfo(it) }
                to?.enterMessage?.let { e.player.prettyInfo(it) }
            }
        }
    }
}