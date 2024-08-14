package com.mcstarrysky.land.util

import org.bukkit.Chunk
import org.bukkit.Location

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
}