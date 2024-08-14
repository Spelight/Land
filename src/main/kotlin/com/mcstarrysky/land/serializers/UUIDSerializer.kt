package com.mcstarrysky.land.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * Land
 * com.mcstarrysky.land.serializers.UUIDSerializer
 *
 * @author mical
 * @since 2024/8/2 22:40
 */
object UUIDSerializer : KSerializer<UUID> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("java.util.UUID")

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}