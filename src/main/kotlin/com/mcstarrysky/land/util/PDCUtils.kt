package com.mcstarrysky.land.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

/**
 * 从 PDC 获取内容
 */
operator fun <T, Z> PersistentDataHolder.get(key: String, type: PersistentDataType<T, Z>): Z? {
    return persistentDataContainer.get(NamespacedKey.minecraft(key), type)
}

/**
 * 向 PDC 设置内容
 */
operator fun <T, Z : Any> PersistentDataHolder.set(key: String, type: PersistentDataType<T, Z>, value: Z) {
    persistentDataContainer.set(NamespacedKey.minecraft(key), type, value)
}

/**
 * 判断 PDC 是否包含某个键
 */
fun <T, Z : Any> PersistentDataHolder.has(key: String, type: PersistentDataType<T, Z>): Boolean {
    return persistentDataContainer.has(NamespacedKey.minecraft(key), type)
}

/**
 * 从 PDC 移除内容
 */
fun PersistentDataHolder.remove(key: String) {
    return persistentDataContainer.remove(NamespacedKey.minecraft(key))
}