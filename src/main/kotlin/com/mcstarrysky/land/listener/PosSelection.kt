package com.mcstarrysky.land.listener

import com.mcstarrysky.land.Land
import com.mcstarrysky.land.LandSettings
import com.mcstarrysky.land.util.ChunkUtils
import com.mcstarrysky.land.util.prettyInfo
import org.bukkit.Chunk
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
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

    val record1 = ConcurrentHashMap<UUID, Chunk>()
    val record2 = ConcurrentHashMap<UUID, Chunk>()

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        val player = e.player
        // 主手
        if (e.isMainhand()) {
            // 是选择棒
            if (player.equipment.itemInMainHand.isSimilar(Land.tool)) {
                when {
                    e.isLeftClickBlock() -> {
                        e.isCancelled = true
                        val pos1 = e.clickedBlock!!.location.chunk
                        // 首先存入刚才的点
                        record1 += player.uniqueId to pos1
                        // 尝试获取第二个点
                        val pos2 = record2[player.uniqueId]
                        if (pos2 == null || pos2.world != pos1.world) {
                            listOf(
                                "+========选择的范围信息========+",
                                "世界: &{#8cc269}" + LandSettings.worldAliases[pos1.world.name],
                                "点1: &{#8cc269}(${pos1.x}, ${pos1.z})",
                                "+===========================+"
                            ).forEach { player.prettyInfo(it) }
                        } else {
                            submitAsync {
                                val chunks = ChunkUtils.getChunksInRectangle(pos1.world, pos1, pos2)
                                listOf(
                                    "+========选择的范围信息========+",
                                    "世界: &{#8cc269}" + LandSettings.worldAliases[pos1.world.name],
                                    "点1: &{#8cc269}(${pos1.x}, ${pos1.z})",
                                    "点2: &{#8cc269}(${pos2.x}, ${pos2.z})",
                                    "范围花费: 每区块3*共${chunks.size}个区块=${3*chunks.size}个开拓水晶",
                                    "+====(输入/land进入领地主菜单)====+"
                                ).forEach { player.prettyInfo(it) }
                            }
                        }
                    }
                    e.isRightClickBlock() -> {
                        e.isCancelled = true
                        val pos2 = e.clickedBlock!!.location.chunk
                        // 首先存入刚才的点
                        record2 += player.uniqueId to pos2
                        // 尝试获取第一个点
                        val pos1 = record1[player.uniqueId]
                        if (pos1 == null || pos1.world != pos2.world) {
                            listOf(
                                "+========选择的范围信息========+",
                                "世界: &{#8cc269}" + LandSettings.worldAliases[pos2.world.name],
                                "点2: &{#8cc269}(${pos2.x}, ${pos2.z})",
                                "+===========================+"
                            ).forEach { player.prettyInfo(it) }
                        } else {
                            submitAsync {
                                val chunks = ChunkUtils.getChunksInRectangle(pos1.world, pos1, pos2)
                                listOf(
                                    "+========选择的范围信息========+",
                                    "世界: &{#8cc269}" + LandSettings.worldAliases[pos1.world.name],
                                    "点1: &{#8cc269}(${pos1.x}, ${pos1.z})",
                                    "点2: &{#8cc269}(${pos2.x}, ${pos2.z})",
                                    "范围花费: 每区块3*共${chunks.size}个区块=${3*chunks.size}个开拓水晶",
                                    "+====(输入/land进入领地主菜单)====+"
                                ).forEach { player.prettyInfo(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}