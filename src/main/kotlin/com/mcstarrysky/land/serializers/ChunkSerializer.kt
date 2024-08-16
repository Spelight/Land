package com.mcstarrysky.land.serializers

import com.mcstarrysky.land.data.LandChunk
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * 保留旧版本区块序列化兼容性
 *
 * @author mical
 * @since 2024/8/2 22:45
 */
object ChunkSerializer : KSerializer<LandChunk> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LandChunk") {
        element<String>("world")
        element<Int>("x")
        element<Int>("z")
    }

    override fun serialize(encoder: Encoder, value: LandChunk) {
        // 全部正常序列化成 Json
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.world)
            encodeIntElement(descriptor, 1, value.x)
            encodeIntElement(descriptor, 2, value.z)
        }
    }

    override fun deserialize(decoder: Decoder): LandChunk {
        val input = decoder.decodeString()

        return if (input.contains("~") && input.contains(",")) {
            // 旧版本格式解析
            val data = input.split("~")
            val world = data[0]
            val (x, z) = data[1].split(",").map { it.toInt() }
            LandChunk(world, x, z)
        } else {
            // 新版本格式解析
            decoder.decodeStructure(descriptor) {
                var world = ""
                var x = 0
                var z = 0
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> world = decodeStringElement(descriptor, 0)
                        1 -> x = decodeIntElement(descriptor, 1)
                        2 -> z = decodeIntElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> throw SerializationException("Unknown index $index")
                    }
                }
                LandChunk(world, x, z)
            }
        }
    }
}