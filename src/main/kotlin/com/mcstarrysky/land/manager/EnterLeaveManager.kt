package com.mcstarrysky.land.manager

import com.mcstarrysky.land.util.prettyInfo
import taboolib.common.platform.Schedule
import taboolib.platform.util.onlinePlayers
import java.util.UUID

/**
 * Land
 * com.mcstarrysky.land.manager.EnterLeaveManager
 *
 * @author mical
 * @since 2024/8/3 19:14
 */
object EnterLeaveManager {

    // 玩家 UUID - 领地编号
    private val previousLand = LinkedHashMap<UUID, Int>()

    @Schedule(period = 14L, async = true) // 700ms
    fun tick() {
        onlinePlayers.forEach { p ->
            val land = LandManager.getLand(p.location)?.id ?: -1
            val pre = previousLand.remove(p.uniqueId) ?: -1
            previousLand += p.uniqueId to land
            if (pre != land) {
                LandManager.getLand(pre)?.leaveMessage?.let { p.prettyInfo(it) }
                LandManager.getLand(land)?.enterMessage?.let { p.prettyInfo(it) }
            }
        }
    }
}