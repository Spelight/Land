package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.*
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.setMeta

/**
 * Land
 * com.mcstarrysky.land.flag.PermMove
 *
 * @author mical
 * @since 2024/8/3 21:07
 */
object PermMove : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "move"

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.IRON_BOOTS) {
            name = "&f移动 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8领地内移动",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @Schedule(period = 14L, async = true)
    fun tick() {
        onlinePlayers.forEach { p ->
            val loc = p.location
            LandManager.getLand(loc)?.run {
                if (!hasPermission(p) && !getFlag(this@PermMove.id)) {
                    val centre = LocationUtils.calculateLandCenter(area, p.world)
                    val l: Location = p.location  // 获取玩家当前位置

                    // 计算移动方向和距离
                    var x: Double = if (l.blockX > centre.blockX) {
                        1  // 如果玩家在地块中心的右侧，向右移动
                    } else {
                        -1  // 如果玩家在地块中心的左侧，向左移动
                    }.toDouble()

                    var z: Double = if (l.blockZ > centre.blockZ) {
                        1  // 如果玩家在地块中心的下方，向下移动
                    } else {
                        -1  // 如果玩家在地块中心的上方，向上移动
                    }.toDouble()

                    val distance = 1.0  // 假设移动距离为10个方块，根据实际情况调整
                    x *= distance  // 计算x方向上的移动距离
                    z *= distance  // 计算z方向上的移动距离

                    // 执行玩家的移动

                    p.setMeta("land_ban_move_teleport", true)
                    p.teleportAsync(l.add(x, 0.0, z))  // 将玩家传送到新的位置
                    p.prettyInfo("没有权限, 禁止移动&7\\(标记: ${this@PermMove.id}\\)")
                }
            }
        }
    }
}