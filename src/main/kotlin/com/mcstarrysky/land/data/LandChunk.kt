package com.mcstarrysky.land.data

import kotlinx.serialization.Serializable
import org.bukkit.Chunk

/**
 * Land
 * com.mcstarrysky.land.data.LandChunk
 *
 * @author mical
 * @since 2024/8/16 14:02
 */
@Serializable
data class LandChunk(
    val world: String,
    val x: Int,
    val z: Int
) {

    constructor(bkChunk: Chunk) : this(bkChunk.world.name, bkChunk.x, bkChunk.z)

    fun isEqualBkChunk(bkChunk: Chunk): Boolean {
        return world == bkChunk.world.name && x == bkChunk.x && z == bkChunk.z
    }
}

fun Chunk.toLandChunk(): LandChunk = LandChunk(this)