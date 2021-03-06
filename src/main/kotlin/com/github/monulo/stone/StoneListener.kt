package com.github.monulo.stone

import com.github.monulo.stone.Stone.respawnDelay
import com.github.monulo.stone.StoneBattlePlugin.Companion.instance
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockIterator
import java.lang.Math.max
import java.lang.Math.sqrt
import kotlin.random.Random.Default.nextInt

class StoneListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Stone.fakeEntityServer.addPlayer(event.player)
        val player = event.player
        if (instance.load(player)) player.gameMode = GameMode.SPECTATOR
    }
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        instance.save(player)
    }
    @EventHandler(ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if(event.player in Stone.respawns && event.cause == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.isCancelled = true
        }
    }
    @EventHandler(ignoreCancelled = true)
    fun onMergeItem(event: ItemMergeEvent) {
        if (event.entity.itemStack.type == Material.COBBLESTONE) event.isCancelled = true
    }
    @EventHandler(ignoreCancelled = true)
    fun onBlockForm(event: BlockFormEvent) {
        val state = event.newState

        if (state.type == Material.COBBLESTONE) {
            state.type = Material.STONE
        }
        if(state.block.type == Material.COBBLESTONE) {
            state.block.type = Material.STONE
        }
    }
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if(item.type == Material.COBBLESTONE) {
            if(event.action == Action.RIGHT_CLICK_AIR) {
                val loc = event.player.location.apply { y -= 0.001; pitch = 0F }
                val iterator = BlockIterator(loc, 0.0, 20)
                while(iterator.hasNext()) {
                    val block = iterator.next()
                    if(block.type.isAir) {
                        block.type = Material.COBBLESTONE
                        block.location.world.playSound(block.location, Sound.BLOCK_STONE_PLACE, SoundCategory.MASTER, 1.0F, 1.0F)
                        item.amount--
                        break
                    }
                }
            } else if(event.action == Action.LEFT_CLICK_AIR) {
                val entity = Stone.fakeEntityServer.spawnEntity(event.player.eyeLocation, ArmorStand::class.java).apply {
                    updateMetadata<ArmorStand> {
                        isInvisible = true
                        isMarker = true
                        isSmall = true
                    }
                    updateEquipment {
                        helmet = ItemStack(Material.COBBLESTONE)
                    }
                }
                val projectile = StoneProjectile(event.player, entity, 400, 256.0)
                projectile.velocity = event.player.eyeLocation.direction.multiply(1.0)
                Stone.fakeProjectileManager.launch(event.player.eyeLocation, projectile)
                item.amount--
            }
        }
    }
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player

        if(Stone.redb.location.distance(event.player.location) > 20) {
            event.player.sendActionBar("??????????????? ?????? ???????????? ???????????? ????????????.")
            } else {
            if(Bukkit.getScoreboardManager().mainScoreboard.getTeam("RED")?.players!!.contains(event.player)) {
                if (block.type == Material.STONE
                    && player.gameMode.let { it == GameMode.SURVIVAL || it == GameMode.ADVENTURE }
                    && block.getDrops(player.inventory.itemInMainHand, player).isNotEmpty()
                ) {
                    event.isDropItems = false

                    val loc = block.location.add(0.5, 0.8, 0.5)
                    val count = max(1, sqrt(nextInt(64).toDouble()).toInt())

                    for (i in 0 until count) {
                        val item = ItemStack(Material.COBBLESTONE)

                        loc.world.dropItem(loc, item).apply {
                            pickupDelay -= i * 2
                        }
                    }
                }
            }
        }
        if(Stone.blueb.location.distance(event.player.location) > 20) {
            if(Bukkit.getScoreboardManager().mainScoreboard.getTeam("BLUE")?.players!!.contains(event.player)) {
                event.player.sendActionBar("??????????????? ?????? ???????????? ???????????? ????????????.")
            } else {
                if (block.type == Material.STONE
                    && player.gameMode.let { it == GameMode.SURVIVAL || it == GameMode.ADVENTURE }
                    && block.getDrops(player.inventory.itemInMainHand, player).isNotEmpty()
                ) {
                    event.isDropItems = false

                    val loc = block.location.add(0.5, 0.8, 0.5)
                    val count = max(1, sqrt(nextInt(64).toDouble()).toInt())

                    for (i in 0 until count) {
                        val item = ItemStack(Material.COBBLESTONE)

                        loc.world.dropItem(loc, item).apply {
                            pickupDelay -= i * 2
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val gameMode = player.gameMode
        if(gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) return
        player.gameMode = GameMode.SPECTATOR
        Stone.respawns[player] = Respawn().apply {
            this.respawnLocation = event.respawnLocation
            this.gameMode = gameMode
            this.respawnTime = TimeTools.machineTimeMillis() + respawnDelay
        }
        for(i in 0..45) {
            val item = event.player.inventory.getItem(i) ?: return
            item.amount = item.amount / 2
        }
    }
}