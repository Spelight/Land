package com.mcstarrysky.land.manager

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.util.*
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import taboolib.common.io.newFile
import taboolib.common.io.newFolder
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.getDataFolder
import java.nio.charset.StandardCharsets
import java.util.LinkedList

/**
 * Land
 * com.mcstarrysky.land.manager.LandManager
 *
 * @author mical
 * @since 2024/8/2 23:06
 */
object LandManager {

//    val defaultFlags = mapOf(
//        "banBreak" to true,
//        "banPlace" to true,
//        "banIgnite" to true,
//        "banDoor" to true,
//        "banContact" to true,
//        "banButton" to true,
//        "banContainer" to true,
//        "banUseOther" to true,
//        "banBucket" to true,
//        "banPvp" to true,
//        "protect" to true,
//        "banPotionHurt" to true
//    )

    val defaultFlags = mapOf(
        "bucket" to false,
        "build" to false,
        "container" to false,
        "entity_explosion" to true,
        "interact" to false,
        "mob_spawn" to true,
        "teleport" to true,
        "move" to true,
        "teleport_in" to true,
        "teleport_out" to true
    )

    val lands = LinkedList<Land>()

    val permissions = LinkedList<Permission>()

    fun import() {
        newFolder(getDataFolder(), "lands").listFiles()?.forEach { file ->
            if (file.extension == "json") {
                lands += json.decodeFromString(Land.serializer(), file.readText(StandardCharsets.UTF_8))
            }
        }
    }

    @Schedule(period = 20 * 60L)
    fun export() {
        lands.forEach(::save)
    }

    fun getLands(player: Player): List<Land> {
        return lands.filter { it.owner == player.uniqueId }
    }

    fun save(land: Land) {
        newFile(getDataFolder(), "lands/${land.id}.json")
            .writeText(land.saveToString(), StandardCharsets.UTF_8)
    }

    fun getLand(location: Location): Land? {
        return lands.firstOrNull { it.isInArea(location) }
    }

    fun getLand(id: Int): Land? {
        return lands.firstOrNull { it.id == id }
    }

    fun hasLand(name: String): Boolean {
        return lands.any { it.name == name }
    }

    fun create(player: Player, name: String, chunk1: Chunk, chunk2: Chunk) {

    }

    fun createFree(player: Player, clickedLocation: Location) {
        if (hasFree(player)) {
            player.prettyInfo("你已经获取过免费领地了.")
            return
        }
        if (player.world.name != "world") {
            player.prettyInfo("免费领地棒只能在[主世界](color=#8abcd1)使用")
            return
        }
        var name = "免费领地_${player.name}"
        // 避免极小概率的领地重复情况
        if (hasLand(name)) {
            name += System.currentTimeMillis()
        }
        val area = ChunkUtils.getCenteredChunks(clickedLocation, 5)
        if (area.any { chunk -> lands.any { it.area.contains(chunk) } }) {
            player.prettyInfo("你的领地区域与现有领地区域重叠, 请重新选择位置!")
            return
        }
//        val centre = LocationUtils.calculateLandCenter(area, player.world)
//        player.prettyInfo("自动设置领地传送点为领地中央位置.")
        val land = Land(
            newId(),
            name,
            player.uniqueId,
            System.currentTimeMillis(),
            clickedLocation.world.name,
            "${player.name} 的免费领地",
            area = area.toMutableList(),
            tpLocation = clickedLocation.add(0.0, 1.0, 0.0),
            cooperators = mutableListOf(),
            users = mutableMapOf()
        )
        for ((flag, value) in defaultFlags) {
            player.prettyInfo("自动添加标记 [{0}](color=#8abcd1) &7\\(值: {1}\\)", flag, value)
            land.flags += flag to value
        }
        lands += land
        player.prettyInfo("创建免费领地成功.")

        player["land_free_created", PersistentDataType.BOOLEAN] = true
    }

    fun hasFree(player: Player): Boolean {
        return player.has("land_free_created", PersistentDataType.BOOLEAN)
    }

    fun newId(): Int {
        return (lands.maxByOrNull { it.id }?.id ?: 0) + 1
    }
}