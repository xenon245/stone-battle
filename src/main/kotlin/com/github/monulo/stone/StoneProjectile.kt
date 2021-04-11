package com.github.monulo.stone

import com.github.monun.tap.fake.FakeEntity
import com.github.monun.tap.fake.FakeProjectile
import com.github.monun.tap.fake.Movement
import com.github.monun.tap.fake.Trail
import com.github.monun.tap.math.normalizeAndLength
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.function.Predicate

class StoneProjectile(val owner: Player, val fakeEntity: FakeEntity, maxTicks: Int, range: Double) : FakeProjectile(maxTicks, range) {
    override fun onPreUpdate() {
        velocity = velocity.multiply(0.99).apply { y -= 0.03 }
    }
    override fun onMove(movement: Movement) {
        fakeEntity.moveTo(movement.to)
    }

    override fun onTrail(trail: Trail) {
        trail.velocity?.let { velocity ->
            val from = trail.from
            val world = from.world
            val length = velocity.normalizeAndLength()
            val filter = Predicate<Entity> {
                when(it) {
                    owner -> false
                    is LivingEntity -> true
                    is Player -> {
                        it.gameMode == GameMode.SURVIVAL || it.gameMode == GameMode.ADVENTURE
                    }
                    else -> false
                }
            }
            world.rayTrace(from, velocity, length, FluidCollisionMode.NEVER, true, 1.0, filter)?.let { result ->
                remove()
                val hitPosition = result.hitPosition
                val hitLocation = hitPosition.toLocation(world)
                hitLocation.block.type = Material.AIR
                val box = BoundingBox.of(hitPosition, 1.5, 2.0, 1.5)
                for(entity in world.getNearbyEntities(box, filter)) {
                    if(entity is LivingEntity) {
                        entity.damage(4.0)
                    }
                }
            }
            world.rayTraceBlocks(from, velocity, length, FluidCollisionMode.NEVER, true)?.let { result ->
                result.hitBlock?.type = Material.AIR
            }
        }
    }

    override fun onRemove() {
        fakeEntity.remove()
    }
}