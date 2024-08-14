package com.mcstarrysky.land

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.conversion
import taboolib.module.configuration.util.getStringColored

/**
 * Land
 * com.mcstarrysky.land.LandSettings
 *
 * @author mical
 * @since 2024/8/3 15:51
 */
object LandSettings {

    @Config(autoReload = true)
    private lateinit var config: Configuration

    @delegate:ConfigNode("Settings.world-aliases")
    val worldAliases: Map<String, String> by conversion<ConfigurationSection, Map<String, String>> {
        getKeys(false).associateWith { getStringColored(it) ?: it }
    }
}