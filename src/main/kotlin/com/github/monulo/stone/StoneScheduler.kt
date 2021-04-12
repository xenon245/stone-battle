package com.github.monulo.stone

import com.github.monulo.stone.StoneBattlePlugin.Companion.instance
import org.bukkit.*
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerTeleportEvent

class StoneScheduler : Runnable {
    private var blue = 0
    private var red = 0
    override fun run() {
        val iterator = Stone.respawns.iterator()
        while(iterator.hasNext()) {
            val entry = iterator.next()
            val respawn = entry.value
            val remain = respawn.remainRespawnTime
            if(remain > 0) {
                entry.key.sendActionBar(String.format("${ChatColor.RED}${ChatColor.BOLD}리스폰까지 %.1f초", remain / 1000.0))
            } else {
                entry.key.run {
                    iterator.remove()
                    sendActionBar(" ")
                    gameMode = respawn.gameMode
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    if(scoreboard.getTeam("RED")?.players!!.contains(this)) {
                        teleport(Location(world, -67.5, 4.0, 6.5, -90.0F, 0.0F), PlayerTeleportEvent.TeleportCause.PLUGIN)
                    }
                    if(scoreboard.getTeam("BLUE")?.players!!.contains(this)) {
                        teleport(Location(world, 84.5, 4.0, 10.5, 90.0F, 0.0F), PlayerTeleportEvent.TeleportCause.PLUGIN)
                    }
                }
            }
        }
        if(!Stone.red.isVisible) {
            Stone.red.isVisible = true
        }
        if(!Stone.blue.isVisible) {
            Stone.blue.isVisible = true
        }
        Stone.fakeEntityServer.update()
        Stone.fakeProjectileManager.update()
        for(player in Bukkit.getOnlinePlayers()) {
            if(player.location.distance(Stone.redb.location) < 20) {
                Stone.red.addPlayer(player)
            } else {
                Stone.red.removePlayer(player)
            }
            if(player.location.distance(Stone.blueb.location) < 20) {
                Stone.blue.addPlayer(player)
            } else {
                Stone.blue.removePlayer(player)
            }
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
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage("${ChatColor.AQUA}BLUE팀이 탈락했습니다.")
            }
            Bukkit.getScheduler().runTask(instance, instance.task::cancel)
            HandlerList.unregisterAll(instance)
            Stone.red.removeAll()
            Stone.blue.removeAll()
        }
        if(red > 1) {
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage("${ChatColor.RED}RED팀이 탈락했습니다.")
            }
            Bukkit.getScheduler().runTask(instance, instance.task::cancel)
            HandlerList.unregisterAll(instance)
            Stone.red.removeAll()
            Stone.blue.removeAll()
        }
    }
}