package com.mcstarrysky.land.manager

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.flag.*
import com.mcstarrysky.land.listener.PosSelection
import com.mcstarrysky.land.util.*
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import taboolib.common.io.newFile
import taboolib.common.io.newFolder
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.getDataFolder
import taboolib.module.chat.Components
import taboolib.platform.util.checkItem
import taboolib.platform.util.takeItem
import java.nio.charset.StandardCharsets
import java.util.LinkedList
import kotlin.math.ceil

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

//    val defaultFlags = mapOf(
//        "bucket" to false,
//        "build" to false,
//        "container" to false,
//        "entity_explosion" to true,
//        "interact" to false,
//        "mob_spawn" to true,
//        "teleport" to true,
//        "move" to true,
//        "teleport_in" to true,
//        "teleport_out" to true
//    )
    val defaultFlags = listOf(
        PermBucket,
        PermBuild,
        PermContainer,
        PermEntityExplosion,
        PermFunctionalBlocks,
        PermInteract,
        PermTeleport
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

    fun create(player: Player, name: String) {
        val chunk1 = PosSelection.pos1[player.uniqueId]
        val chunk2 = PosSelection.pos2[player.uniqueId]
        if (chunk1 == null || chunk2 == null) {
            player.prettyInfo("你尚未选择点! 请先用领地棒选择两个点!")
            return
        }

        if (chunk1.world != chunk2.world) {
            player.prettyInfo("你选择的两个区块必须要在同一个世界!")
            return
        }


        val area = ChunkUtils.getChunksInRectangle(chunk1.world, chunk1, chunk2)
        if (area.any { chunk -> lands.any { it.area.contains(chunk) } }) {
            player.prettyInfo("你的领地区域与现有领地区域重叠, 请重新选择位置!")
            return
        }

        if (!name.isValidLandName()) {
            player.prettyInfo("为避免与领地编号混淆, 名字不能是纯数字!")
            return
        }
        if (hasLand(name)) {
            player.prettyInfo("你输入的名字已存在! 请重新操作")
            return
        }

        val crystalNeeds = area.size * 3
        if (player.checkItem(com.mcstarrysky.land.Land.crystal, crystalNeeds)) {
            player.inventory.takeItem(crystalNeeds) { it.isSimilar(com.mcstarrysky.land.Land.crystal) }
        } else {
            // 这里用到一个奇怪的操作
            Components.parseRaw(
                GsonComponentSerializer.gson()
                    .serialize(
                        GsonComponentSerializer.gson()
                            .deserialize(cacheMessageWithPrefix("抱歉, 你要准备 $crystalNeeds 个 ").toRawMessage())
                            .append(
                                LegacyComponentSerializer.legacyAmpersand().deserialize("&b开拓水晶")
                                    .hoverEvent(com.mcstarrysky.land.Land.crystal.clone().asHoverEvent())
                            )
                    )
            ).append(cacheMessageWithPrefixColor(" 才能占领你选中的这 ${area.size} 个区块"))
                .sendTo(adaptPlayer(player))
            return
        }

        val centre = LocationUtils.calculateLandCenter(area, player.world)
        player.prettyInfo("自动设置领地传送点为领地中央位置.")
        val land = Land(
            newId(),
            name,
            player.uniqueId,
            System.currentTimeMillis(),
            centre.world.name,
            name,
            area = area.toMutableList(),
            tpLocation = centre,
//            cooperators = mutableListOf(),
            users = mutableMapOf()
        )
        for (perm in defaultFlags) {
            val flag = perm.id
            val value = perm.default
            player.prettyInfo("自动添加标记 [{0}](color=#8abcd1) &7\\(值: {1}\\)", flag, value)
            land.flags += flag to value
        }
        lands += land
        player.prettyInfo("创建领地 (ID: ${land.id}) 成功.")
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
//            cooperators = mutableListOf(),
            users = mutableMapOf()
        )
        for (perm in defaultFlags) {
            val flag = perm.id
            val value = perm.default
            player.prettyInfo("自动添加标记 [{0}](color=#8abcd1) &7\\(值: {1}\\)", flag, value)
            land.flags += flag to value
        }
//        for ((flag, value) in defaultFlags) {
//            player.prettyInfo("自动添加标记 [{0}](color=#8abcd1) &7\\(值: {1}\\)", flag, value)
//            land.flags += flag to value
//        }
        lands += land
        player.prettyInfo("创建免费领地 (ID: ${land.id}) 成功.")

        player["land_free_created", PersistentDataType.BOOLEAN] = true
    }

    fun hasFree(player: Player): Boolean {
        return player.has("land_free_created", PersistentDataType.BOOLEAN)
    }

    fun newId(): Int {
        return (lands.maxByOrNull { it.id }?.id ?: 0) + 1
    }
}