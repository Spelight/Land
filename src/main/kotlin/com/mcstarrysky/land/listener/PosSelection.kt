package com.mcstarrysky.land.listener

import com.mcstarrysky.land.Land
import com.mcstarrysky.land.LandSettings
import com.mcstarrysky.land.util.ChunkUtils
import com.mcstarrysky.land.util.prettyInfo
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
                    e.isLeftClickBlock() -> {
                        val pos = e.clickedBlock!!.location.chunk
                        pos1.computeIfAbsent(player.uniqueId) { pos }
                        if (pos2[player.uniqueId] == null) {
                            player.prettyInfo("+========选择的范围信息========+[](br)" +
                                    "世界: &b" + LandSettings.worldAliases[pos.world.name] + "[](br)" +
                                    "点1: &b(${pos.x}, ${pos.z})[](br)" +
                                    "+===================================+")
                                    //"+==(输入/land进入领地主菜单)==+")
                        } else {
                            val p2 = pos2[player.uniqueId]!!
                            val chunks = ChunkUtils.getChunksInRectangle(pos.world, pos, p2)
                            player.prettyInfo("+========选择的范围信息========+[](br)" +
                                    "世界: &b" + LandSettings.worldAliases[pos.world.name] + "[](br)" +
                                    "点1: &b(${pos.x}, ${pos.z})[](br)" +
                                    "点2: &b(${p2.x}, ${p2.z})[](br)" +
                                    "范围花费: 每区块3*区块数${chunks.size}=${3*chunks.size}个开拓水晶[](br)" +
                                    "+==(输入/land进入领地主菜单)==+")
                        }
                    }
                    e.isRightClickBlock() -> {
                        val pos = e.clickedBlock!!.location.chunk
                        pos2.computeIfAbsent(player.uniqueId) { pos }
                        if (pos1[player.uniqueId] == null) {
                            player.prettyInfo("+========选择的范围信息========+[](br)" +
                                    "世界: &b" + LandSettings.worldAliases[pos.world.name] + "[](br)" +
                                    "点2: &b(${pos.x}, ${pos.z})[](br)" +
                                    "+===================================+")
                            //"+==(输入/land进入领地主菜单)==+")
                        } else {
                            val p1 = pos1[player.uniqueId]!!
                            val chunks = ChunkUtils.getChunksInRectangle(pos.world, pos, p1)
                            player.prettyInfo("+========选择的范围信息========+[](br)" +
                                    "世界: &b" + LandSettings.worldAliases[pos.world.name] + "[](br)" +
                                    "点1: &b(${p1.x}, ${p1.z})[](br)" +
                                    "点2: &b(${pos.x}, ${pos.z})[](br)" +
                                    "范围花费: 每区块3*区块数${chunks.size}=${3*chunks.size}个开拓水晶[](br)" +
                                    "+==(输入/land进入领地主菜单)==+")
                        }
                    }
                }
            }
        }
    }
}