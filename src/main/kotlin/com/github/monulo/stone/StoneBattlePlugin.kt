package com.github.monulo.stone

import com.github.monun.kommand.kommand
import com.github.monun.tap.fake.FakeEntityServer
import com.github.monun.tap.fake.FakeProjectileManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class StoneBattlePlugin : JavaPlugin() {
    lateinit var task: BukkitTask
    override fun onEnable() {
        Stone.fakeProjectileManager = FakeProjectileManager()
        Stone.fakeEntityServer = FakeEntityServer.create(this)
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
                            blue.player?.teleport(Location(blue.player!!.world, 84.5, 4.0, 10.5, 90.0F, 0.0F))
                        }
                        for(red in Bukkit.getScoreboardManager().mainScoreboard.getTeam("RED")?.players!!) {
                            red.player?.teleport(Location(red.player!!.world, -67.5, 4.0, 6.5, -90.0F, 0.0F))
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
        stop()
    }
}