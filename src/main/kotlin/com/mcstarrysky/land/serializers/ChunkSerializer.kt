package com.mcstarrysky.land.serializers

import com.mcstarrysky.land.util.deserializeToChunk
import com.mcstarrysky.land.util.serializeToString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Chunk

/**
 * Land
 * com.mcstarrysky.land.serializers.ChunkSerializer
 *
 * @author mical
 * @since 2024/8/2 22:45
 */
object ChunkSerializer : KSerializer<Chunk> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.bukkit.Chunk", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Chunk) {
        encoder.encodeString(value.serializeToString())
    }

    override fun deserialize(decoder: Decoder): Chunk {
        return decoder.decodeString().deserializeToChunk()
    }
}