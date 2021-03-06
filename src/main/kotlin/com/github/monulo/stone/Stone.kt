package com.github.monulo.stone

import com.github.monun.tap.fake.FakeEntityServer
import com.github.monun.tap.fake.FakeProjectileManager
import org.bukkit.Bukkit
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object Stone {
    lateinit var fakeEntityServer: FakeEntityServer
    lateinit var fakeProjectileManager: FakeProjectileManager
    var redb = Bukkit.getServer().worlds.first().getBlockAt(-75, 9, 6)
    var blueb = Bukkit.getServer().worlds.first().getBlockAt(91, 9, 10)
    lateinit var red: BossBar
    lateinit var blue: BossBar
    lateinit var respawnFolder: File

    var respawnDelay: Long = 0L
    lateinit var respawns: IdentityHashMap<Player, Respawn>
}