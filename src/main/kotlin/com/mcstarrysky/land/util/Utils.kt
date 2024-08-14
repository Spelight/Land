package com.mcstarrysky.land.util

import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.command.CommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.util.replaceWithOrder
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.component
import java.text.SimpleDateFormat
import java.util.UUID

/**
 * Land
 * com.mcstarrysky.land.LandUtils
 *
 * @author mical
 * @since 2024/8/2 22:33
 */
val ZERO_UUID = UUID(0, 0)

fun Permission.registerPermission() {
    if (LandManager.permissions.none { it.id == id }) {
        LandManager.permissions += this
    }
}

fun String.isValidLandName(): Boolean {
    // 判断名字是否为纯数字
    if (this.all { it.isDigit() }) {
        return false
    }
    // 如果名字不是纯数字，则认为是合法的
    return true
}

val Boolean?.display: String
    get() = if (this == null) "§e未设置" else if (this) "§a允许" else "§c阻止"

val json by unsafeLazy {
    Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        allowStructuredMapKeys = true
    }
}

fun Location.serializeToString(): String {
    return "${world.name}~$x,$y,$z~$yaw,$pitch"
}

fun String.deserializeToLocation(): Location {
    val (world, pos, rotation) = split("~")
    val (x, y, z) = pos.split(",").map { it.toDouble() }
    val (yaw, pitch) = rotation.split(",").map { it.toFloat() }
    return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}

fun Chunk.serializeToString(): String {
    return "${world.name}~$x,$z"
}

fun String.deserializeToChunk(): Chunk {
    val (world, pos) = split("~")
    val (x, z) = pos.split(",").map { it.toInt() }
    return Bukkit.getWorld(world)!!.getChunkAt(x, z)
}

private const val color = "#1af1aa"

private val prefix = whiteColorCode(color)

fun CommandSender.prettyInfo(message: String, vararg args: Any) {
    cacheMessageWithPrefix(message, *args).sendTo(adaptCommandSender(this))
}

fun cacheMessageWithPrefix(message: String, vararg args: Any) =
    "&8\\[&{${color}}领地&8\\] &{${prefix}}$message".replaceWithOrder(*args)
        .component().buildColored()

fun cacheMessageWithPrefixColor(message: String, vararg args: Any) =
    "&{${prefix}}$message".replaceWithOrder(*args).component().buildColored()

val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")