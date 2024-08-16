package com.mcstarrysky.land.util

import com.mcstarrysky.land.data.LandChunk
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World

/**
 * Land
 * com.mcstarrysky.land.util.LocationUtils
 *
 * @author mical
 * @since 2024/8/3 14:07
 */
object LocationUtils {

    /**
     * 计算领地中心的位置，确保在任何情况下都能返回一个有效的地面位置。
     * @param chunks 区块列表
     * @param world 世界对象
     * @return 地面上的一个位置
     * @throws IllegalArgumentException 如果区块列表为空
     */
    fun calculateLandCenter(chunks: List<LandChunk>, world: World): Location {
        require(chunks.isNotEmpty()) { "区块列表不能为空" }

        var totalX = 0.0
        var totalZ = 0.0

        // 计算总的 X 和 Z 坐标
        for (chunk in chunks) {
            val chunkCenter = getChunkCenter(chunk)
            totalX += chunkCenter.x
            totalZ += chunkCenter.z
        }

        // 计算平均 X 和 Z 坐标
        val avgX = totalX / chunks.size
        val avgZ = totalZ / chunks.size

        // 从最高方块到海平面开始搜索地面位置
        val highestY = world.getHighestBlockYAt(avgX.toInt(), avgZ.toInt())

        var groundY = 0
        for (y in highestY downTo 0) {
            val testLocation = Location(world, avgX, y.toDouble(), avgZ)
            if (isLocationOnGround(testLocation)) {
                groundY = y
                break
            }
        }

        // 如果未找到地面位置，则返回海平面位置
        return Location(world, avgX, groundY.toDouble(), avgZ)
    }

    /**
     * 获取区块中心的位置
     * @param chunk 区块对象
     * @return 区块中心的位置
     */
    fun getChunkCenter(chunk: LandChunk): Location {
        val x = (chunk.x shl 4) + 8
        val z = (chunk.z shl 4) + 8
        return Location(Bukkit.getWorld(chunk.world), x.toDouble(), 0.0, z.toDouble())
    }

    /**
     * 检查位置是否在地面上
     * @param location 待检查的位置
     * @return true 如果位置在地面上，否则 false
     */
    fun isLocationOnGround(location: Location): Boolean {
        val block = location.block
        return block.type.isSolid  // 可根据具体需求修改条件，判断是否为地面方块
    }
}