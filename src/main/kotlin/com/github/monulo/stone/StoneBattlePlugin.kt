package com.github.monulo.stone

import com.github.monun.kommand.kommand
import com.github.monun.tap.fake.FakeEntityServer
import com.github.monun.tap.fake.FakeProjectileManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.*

class StoneBattlePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: StoneBattlePlugin
    }
    lateinit var task: BukkitTask
    override fun onEnable() {
        Stone.respawnFolder = File(dataFolder, "respawns").also(File::mkdirs)
        saveDefaultConfig()
        Stone.respawnDelay = config.getLong("respawn-delay")
        Stone.respawns = IdentityHashMap()
        instance = this
        Stone.fakeProjectileManager = FakeProjectileManager()
        Stone.fakeEntityServer = FakeEntityServer.create(this)
        for (player in Bukkit.getOnlinePlayers()) {
            if (load(player)) player.gameMode = GameMode.SPECTATOR
        }
        kommand {
            register("sb") {
                then("start") {
                    executes {
                        Bukkit.getPluginManager().registerEvents(StoneListener(), this@StoneBattlePlugin)
                        task = Bukkit.getScheduler().runTaskTimer(this@StoneBattlePlugin, StoneScheduler(), 0L, 1L)
                        for(player in Bukkit.getOnlinePlayers()) {
                            Stone.fakeEntityServer.addPlayer(player)
                        }
                        for(blue in Bukkit.getScoreboardManager().mainScoreboard.getTeam("BLUE")?.players!!) {
                            blue.player?.teleport(Location(blue.player!!.world, 84.5, 4.0, 10.5, 90.0F, 0.0F), PlayerTeleportEvent.TeleportCause.PLUGIN)
                            blue.player?.bedSpawnLocation = Location(blue.player!!.world, 84.5, 4.0, 10.5, 90.0F, 0.0F)
                        }
                        for(red in Bukkit.getScoreboardManager().mainScoreboard.getTeam("RED")?.players!!) {
                            red.player?.teleport(Location(red.player!!.world, -67.5, 4.0, 6.5, -90.0F, 0.0F), PlayerTeleportEvent.TeleportCause.PLUGIN)
                            red.player?.bedSpawnLocation = Location(red.player!!.world, -67.5, 4.0, 6.5, -90.0F, 0.0F)
                        }
                        Stone.red = Bukkit.createBossBar("RED", BarColor.RED, BarStyle.SEGMENTED_10).apply {
                            isVisible = false
                        }
                        Stone.blue = Bukkit.createBossBar("BLUE", BarColor.BLUE, BarStyle.SEGMENTED_10).apply {
                            isVisible = false
                        }
                        Stone.red.progress = 1.0
                        Stone.blue.progress = 1.0
                        (it.sender as Player).sendMessage("시작")
                        for(player in Bukkit.getOnlinePlayers()) {
                            Stone.red.addPlayer(player)
                            Stone.blue.addPlayer(player)
                        }
                    }
                }
                then("stop") {
                    executes {
                        stop()
                    }
                }
            }
        }
    }
    fun stop() {
        Bukkit.getScheduler().runTask(this@StoneBattlePlugin, task::cancel)
        HandlerList.unregisterAll(this@StoneBattlePlugin)
        Stone.red.removeAll()
        Stone.blue.removeAll()
    }
    override fun onDisable() {
        Stone.red.removeAll()
        Stone.blue.removeAll()
        Bukkit.getOnlinePlayers().forEach(this::save)
        stop()
    }
    private val Player.respawnFile: File
        get() = File(Stone.respawnFolder, "$uniqueId.yml")
    fun load(player: Player): Boolean {
        val file = player.respawnFile
        if(file.exists()) {
            val config = YamlConfiguration.loadConfiguration(file)
            val respawn = Respawn().also { it.load(config) }
            file.delete()
            if(respawn.remainRespawnTime > 0) {
                Stone.respawns[player] = respawn
                return true
            }
        }
        return false
    }
    fun save(player: Player) {
        Stone.respawns.remove(player)?.let { respawn ->
            val config = respawn.save()
            config.save(player.respawnFile)
        }
    }
}