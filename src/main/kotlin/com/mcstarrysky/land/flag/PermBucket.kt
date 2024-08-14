package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.display
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.event.player.PlayerBucketEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermBucket
 *
 * @author mical
 * @since 2024/8/3 17:40
 */
object PermBucket : Permission {

//    @Awake(LifeCycle.ENABLE)
//    private fun init() {
//        registerPermission()
//    }

    override val id: String
        get() = "bucket"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land): ItemStack {
        return buildItem(XMaterial.BUCKET) {
            name = "&f使用桶 ${land.getFlagOrNull(id).display}"
            lore += listOf(
                "&7允许行为:",
                "&8使用桶",
                "",
                "&e左键修改值, 右键取消设置"
            )
            if (land.getFlagOrNull(id) == true) shiny()
            colored()
        }
    }

//    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerBucketEvent) {
        LandManager.getLand(e.player.location)?.run {
            if (!hasPermission(e.player) && !getFlag(this@PermBucket.id)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止水/岩浆桶倒/装7\\(标记: ${this@PermBucket.id}\\)")
            }
        }
    }
}