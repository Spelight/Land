package com.mcstarrysky.land.util

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import kotlin.math.abs

/**
 * Land
 * com.mcstarrysky.land.util.ChunkUtils
 *
 * @author mical
 * @since 2024/8/3 14:03
 */
object ChunkUtils {

    fun getCenteredChunks(location: Location, m: Int): List<Chunk> {
        val chunks = mutableListOf<Chunk>()
        val playerChunk = location.chunk
        val playerChunkX = playerChunk.x
        val playerChunkZ = playerChunk.z
        val radius = m / 2

        // Iterate through an m*m grid of chunks around the player's chunk
        for (dz in -radius..radius) {  // Iterate vertically
            for (dx in -radius..radius) {  // Iterate horizontally
                val chunk = location.world.getChunkAt(playerChunkX + dx, playerChunkZ + dz)
                chunks.add(chunk)
            }
        }

        return chunks
    }

    fun getChunksInRectangle(world: World, chunk1: Chunk, chunk2: Chunk): List<Chunk> {
        // 获取 chunk1 和 chunk2 的边界
        val minX = minOf(chunk1.x, chunk2.x)
        val minZ = minOf(chunk1.z, chunk2.z)
        val maxX = maxOf(chunk1.x, chunk2.x)
        val maxZ = maxOf(chunk1.z, chunk2.z)

        val chunks = mutableListOf<Chunk>()

        // 遍历矩形内的所有 Chunk
        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                val chunk = world.getChunkAt(x, z)
                chunks.add(chunk)
            }
        }

        return chunks
    }

    fun isAdjacentToAnyChunk(chunk: Chunk, chunkList: List<Chunk>): Boolean {
        return chunkList.all { it.world == chunk.world } && chunkList.any { neighborChunk ->
            val dx = abs(chunk.x - neighborChunk.x)
            val dz = abs(chunk.z - neighborChunk.z)
            (dx <= 1 && dz <= 1) && (dx != 0 || dz != 0)
        }
    }
}