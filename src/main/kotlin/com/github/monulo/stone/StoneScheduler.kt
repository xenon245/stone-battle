package com.github.monulo.stone

class StoneScheduler : Runnable {
    override fun run() {
        Stone.fakeEntityServer.update()
        Stone.fakeProjectileManager.update()
    }
}