package com.github.monulo.stone

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockIterator

class StoneListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Stone.fakeEntityServer.addPlayer(event.player)
    }
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if(item.type == Material.STONE) {
            if(event.action == Action.RIGHT_CLICK_AIR) {
                val iterator = BlockIterator(event.player.location, 0.0, 20)
                while(iterator.hasNext()) {
                    val block = iterator.next()
                    if(block.type.isAir) {
                        block.type = Material.STONE
                        block.location.world.playSound(block.location, Sound.BLOCK_STONE_PLACE, SoundCategory.MASTER, 1.0F, 1.0F)
                        item.amount--
                        break
                    }
                }
            } else if(event.action == Action.LEFT_CLICK_AIR) {
                val entity = Stone.fakeEntityServer.spawnEntity(event.player.location, ArmorStand::class.java).apply {
                    updateMetadata<ArmorStand> {
                        isInvisible = true
                        isMarker = true
                    }
                    updateEquipment {
                        helmet = ItemStack(Material.STONE)
                    }
                }
                val projectile = StoneProjectile(entity, 400, 256.0)
                projectile.velocity = event.player.location.direction.multiply(6)
                Stone.fakeProjectileManager.launch(event.player.location, projectile)
            }
        }
    }
}