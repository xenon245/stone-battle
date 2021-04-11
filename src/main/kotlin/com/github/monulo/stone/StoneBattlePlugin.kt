package com.github.monulo.stone

import com.github.monun.kommand.kommand
import com.github.monun.tap.fake.FakeEntityServer
import com.github.monun.tap.fake.FakeProjectileManager
import org.bukkit.Bukkit
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
                    }
                }
            }
        }
    }
}