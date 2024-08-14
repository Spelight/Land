package com.mcstarrysky.land.serializers

import com.mcstarrysky.land.util.deserializeToLocation
import com.mcstarrysky.land.util.serializeToString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Location

/**
 * Land
 * com.mcstarrysky.land.serializers.LocationSerializer
 *
 * @author mical
 * @since 2024/8/2 22:41
 */
object LocationSerializer : KSerializer<Location> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.bukkit.Location", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeString(value.serializeToString())
    }

    override fun deserialize(decoder: Decoder): Location {
        return decoder.decodeString().deserializeToLocation()
    }
}