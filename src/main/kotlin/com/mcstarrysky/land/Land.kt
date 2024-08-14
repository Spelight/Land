package com.mcstarrysky.land

import com.mcstarrysky.land.manager.LandManager
import org.bukkit.inventory.ItemStack
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.Land
 *
 * @author mical
 * @since 2024/8/2 22:20
 */
@RuntimeDependencies(
    RuntimeDependency(
        "org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3",
        test = "!kotlinx.serialization.Serializer",
        relocate = ["!kotlin.", "!kotlin1822."]
    ),
    RuntimeDependency(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3",
        test = "!kotlinx.serialization.json.Json",
        relocate = ["!kotlin.", "!kotlin1822."]
    )
)
object Land : Plugin() {

    val tool: ItemStack = buildItem(XMaterial.STICK) {
        name = "&a选择棒"
        lore += listOf(
            "&7左键任意方块选择区块1",
            "&7右键任意方块选择区块2",
            "&7输入&{#8abcd1}/land&7进入领地主菜单"
        )
        colored()
    }

    val crystal: ItemStack = buildItem(XMaterial.AMETHYST_SHARD) {
        name = "&{#D8D8FA}开拓水晶"
        lore += listOf(
            "&7凭借&a3&7个开拓水晶",
            "&7可以占领一个新区块",
            "&7在你的领地信息菜单点击占领按钮来占领"
        )
        colored()
    }

    val freeLandTool: ItemStack = buildItem(XMaterial.ARROW) {
        name = "&b免费领地棒"
        lore += listOf(
            "&7点击方块即可免费创建并获取一个领地",
            "&7领地大小为5*5区块",
            "&7领地中心为你点击的方块的位置",
            "&7只能在&{#8abcd1}主世界&7使用,且只能使用一次",
            "&7领地大小获取后可自行扩展"
        )
        colored()
    }

    override fun onEnable() {
        LandManager.import()
        // InventoryHandler.instance
    }

    override fun onDisable() {
        LandManager.export()
    }
}