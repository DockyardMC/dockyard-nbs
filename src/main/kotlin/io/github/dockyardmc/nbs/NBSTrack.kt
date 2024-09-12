package io.github.dockyardmc.nbs

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.player.Player

class NBSTrack(var nbsFile: NBSFile) {
    val playing: Bindable<Boolean> = Bindable(false)
    val looping: Bindable<Boolean> = Bindable(false)
    var currentTick: Int = 0
    val volume: Float = 1f
    val listeners: BindableList<Player> = BindableList()

    fun tick() {

    }

    fun start() {

    }

    fun stop() {

    }

    fun seek(ticks: Int) {

    }

    fun reset() {

    }

    fun fadeTo(float: Float, easing: Float) {

    }

    fun fadeInFromZero(easing: Float) {

    }

    fun fadeOutToZero(easing: Float) {

    }
}
