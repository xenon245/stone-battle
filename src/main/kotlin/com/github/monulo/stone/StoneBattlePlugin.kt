package com.github.monulo.stone

import com.github.monun.kommand.kommand
import com.github.monun.tap.fake.FakeEntityServer
import com.github.monun.tap.fake.FakeProjectileManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class StoneBattlePlugin : JavaPlugin() {
    override fun onEnable() {
        Stone.fakeProjectileManager = FakeProjectileManager()
        Stone.fakeEntityServer = FakeEntityServer.create(this)
        kommand {
            register("sb") {
                then("start") {
                    executes {
                        Bukkit.getPluginManager().registerEvents(StoneListener(), this@StoneBattlePlugin)
                        Bukkit.getScheduler().runTaskTimer(this@StoneBattlePlugin, StoneScheduler(), 0L, 1L)
                        for(player in Bukkit.getOnlinePlayers()) {
                            Stone.fakeEntityServer.addPlayer(player)
                        }
                        for(blue in Bukkit.getScoreboardManager().mainScoreboard.getTeam("BLUE")?.players!!) {
                            blue.player?.teleport(Location(blue.player!!.world, 84.5, 4.0, 10.5, 90.0F, 0.0F))
                        }
                        for(red in Bukkit.getScoreboardManager().mainScoreboard.getTeam("RED")?.players!!) {
                            red.player?.teleport(Location(red.player!!.world, -67.5, 4.0, 6.5, -90.0F, 0.0F))
                        }
                        (it.sender as Player).sendMessage("시작")
                    }
                }
            }
        }
    }
}