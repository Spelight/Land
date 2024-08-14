package com.mcstarrysky.land.util

import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common5.util.decodeBase64
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyMeta
import java.net.URL
import java.util.*

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
 * 旧版 JsonParser
 * 旧版没有 parseString 静态方法
 */
val JSON_PARSER = JsonParser()

infix fun ItemStack.textured(headBase64: String): ItemStack {
    return modifyMeta<SkullMeta> {
        if (MinecraftVersion.majorLegacy >= 12000) {
            val profile = Bukkit.createProfile(UUID.randomUUID(), "TabooLib")
            val textures = profile.textures
            textures.skin = URL(getTextureURLFromBase64(headBase64))
            profile.setTextures(textures)
            playerProfile = profile
        } else {
            val profile = GameProfile(UUID.randomUUID(), "TabooLib")
            val texture = if (headBase64.length in 60..100) encodeTexture(headBase64) else headBase64
            profile.properties.put("textures", Property("textures", texture, "Aiyatsbus_TexturedSkull"))

            setProperty("profile", profile)
        }
    }
}

@Suppress("HttpUrlsUsage")
fun encodeTexture(input: String): String {
    return with(Base64.getEncoder()) {
        encodeToString("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/$input\"}}}".toByteArray())
    }
}

private fun getTextureURLFromBase64(headBase64: String): String {
    return JSON_PARSER
        .parse(String(headBase64.decodeBase64()))
        .asJsonObject
        .getAsJsonObject("textures")
        .getAsJsonObject("SKIN")
        .get("url")
        .asString
}