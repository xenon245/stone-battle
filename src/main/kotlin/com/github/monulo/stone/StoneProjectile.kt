package com.github.monulo.stone

import com.github.monun.tap.fake.FakeEntity
import com.github.monun.tap.fake.FakeProjectile
import com.github.monun.tap.fake.Movement
import com.github.monun.tap.fake.Trail
import com.github.monun.tap.math.normalizeAndLength
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.function.Predicate

class StoneProjectile(val fakeEntity: FakeEntity, maxTicks: Int, range: Double) : FakeProjectile(maxTicks, range) {
    override fun onPreUpdate() {
        velocity = velocity.multiply(0.99).apply { y -= 1.1 }
    }
    override fun onMove(movement: Movement) {
        fakeEntity.moveTo(movement.to)
    }

    override fun onTrail(trail: Trail) {
        val velocity = trail.velocity ?: return
        val from = trail.from
        val world = from.world
        val length = velocity.normalizeAndLength()
        val filter = Predicate<Entity> {
            when(it) {
                is LivingEntity -> true
                is Player -> true
                else -> false
            }
        }
        world.rayTrace(from, velocity, length, FluidCollisionMode.NEVER, true, 1.0, filter)?.let {
            (it.hitEntity as Damageable).damage(1.0)
            it.hitBlock?.type = Material.AIR
        }
    }

    override fun onRemove() {
        fakeEntity.remove()
    }
}