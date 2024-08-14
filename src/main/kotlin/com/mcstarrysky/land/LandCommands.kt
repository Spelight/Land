package com.mcstarrysky.land

import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.menu.LandMainMenu
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.remove
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.command.player

/**
 * Land
 * com.mcstarrysky.land.LandCommands
 *
 * @author mical
 * @since 2024/8/3 14:18
 */
object LandCommands {

    @Awake(LifeCycle.ENABLE)
    fun commandLand() {
        command("land", permission = "starrysky.land", permissionDefault = PermissionDefault.TRUE) {
            execute<Player> { sender, _, _ ->
                LandMainMenu.openMenu(sender)
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun commandGo() {
        command("go", permission = "starrysky.go", permissionDefault = PermissionDefault.TRUE) {
            dynamic("id") {
                suggestionUncheck<Player> { sender, _ ->
                    val lands = LandManager.getLands(sender)
                    lands.map { it.id.toString() }.toMutableList().also { lands.map { it.name } }
                }
                exec<Player> {
                    val id = ctx["id"]
                    try {
                        val land = LandManager.lands.firstOrNull { it.id == id.toInt() }
                        if (land == null) {
                            sender.prettyInfo("指定 ID 的领地不存在")
                            return@exec
                        }
                        land.teleport(sender)
                    } catch (_: NumberFormatException) {
                        val land = LandManager.lands.firstOrNull { it.name == id }
                        if (land == null) {
                            sender.prettyInfo("指定名字的领地不存在(请检查大小写)")
                            return@exec
                        }
                        land.teleport(sender)
                    }
                }
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun commandTest() {
        command("landpdc", permission = "admin") {
            exec<Player> {
                sender.inventory.addItem(Land.tool.clone())
                sender.inventory.addItem(Land.crystal.clone())
                sender.inventory.addItem(Land.freeLandTool.clone())
            }
            dynamic("player") {
                exec<CommandSender> {
                    val player = Bukkit.getPlayerExact(ctx["player"]) ?: return@exec
                    player.remove("land_free_created")
                }
            }
        }
        command("landdelete", permission = "admin") {
            exec<Player> {
                val land = LandManager.getLand(sender.location) ?: return@exec
                land.area -= sender.location.chunk
            }
        }
    }
}