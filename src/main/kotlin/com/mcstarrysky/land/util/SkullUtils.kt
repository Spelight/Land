package com.mcstarrysky.land.util

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.profiles.builder.XSkull
import taboolib.library.xseries.profiles.objects.Profileable
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyMeta

/**
 * Land
 * com.mcstarrysky.land.util.SkullUtils
 *
 * @author mical
 * @since 2024/8/3 16:52
 */
fun ItemStack.skull(skull: String?): ItemStack {
    skull ?: return this
    if (this.isAir) return this
    if (itemMeta !is SkullMeta) return this
    return if (skull.length <= 20) modifyMeta<SkullMeta> { owner = skull }
    else textured(skull)
}

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.ui.internal.function.Skull
 *
 * @author mical
 * @since 2024/2/18 12:21
 */
infix fun ItemStack.textured(headBase64: String): ItemStack {
    return modifyMeta<SkullMeta> {
        XSkull.of(this).profile(Profileable.detect(headBase64)).lenient().apply()
    }
}