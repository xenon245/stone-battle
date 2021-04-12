package com.github.monulo.stone

import org.bukkit.*
import org.bukkit.event.HandlerList

class StoneScheduler : Runnable {
    private var blue = 0
    private var red = 0
    override fun run() {
        Stone.fakeEntityServer.update()
        Stone.fakeProjectileManager.update()
        for(player in Bukkit.getOnlinePlayers()) {
            Stone.red.isVisible = player.location.distance(Stone.redb.location) < 20
            Stone.blue.isVisible = player.location.distance(Stone.blueb.location) < 20
        }
        if(Stone.blueb.type == Material.AIR) {
            if(Stone.blue.progress > 0.01) {
                Stone.blueb.type = Material.LIGHT_BLUE_CONCRETE
                Stone.blue.progress -= 0.01
                Stone.blueb.world.spawnParticle(Particle.BLOCK_CRACK, Stone.blueb.location.x + 0.5, Stone.blueb.location.y, Stone.blueb.location.z + 0.5, 20, 0.1, 0.1, 0.1, Stone.blueb.blockData)
            }
            if(Stone.blue.progress <= 0.01) {
                Stone.blue.progress = 0.0
                blue++
            }
        }
        if(Stone.redb.type == Material.AIR) {
            if(Stone.red.progress > 0.01) {
                Stone.redb.type = Material.RED_CONCRETE
                Stone.red.progress -= 0.01
                Stone.redb.world.spawnParticle(Particle.BLOCK_CRACK, Stone.redb.location.x + 0.5, Stone.redb.location.y, Stone.redb.location.z + 0.5, 20, 0.1, 0.1, 0.1, Stone.redb.blockData)
            }
            if(Stone.red.progress <= 0.01) {
                Stone.red.progress = 0.0
                red++
            }
        }
        if(blue > 1) {
            StoneBattlePlugin().stop()
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage("${ChatColor.AQUA}BLUE팀이 탈락했습니다.")
            }
        }
        if(red > 1) {
            StoneBattlePlugin().stop()
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage("${ChatColor.RED}RED팀이 탈락했습니다.")
            }
        }
    }
}