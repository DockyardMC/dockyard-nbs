package io.github.dockyardmc.nbs

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.runnables.timedSequenceAsync
import io.github.dockyardmc.sounds.playSound
import kotlin.math.pow

class NBSTrack(val nbsFile: NBSFile) {
    var isPlaying: Boolean = false
    val looping: Bindable<Boolean> = Bindable(false)
    var currentTick: Int = if(DockyardNBSConfig.tracksStartAtFirstNote) nbsFile.notes.getFirst().first - 1 else 0
    var volume: Float = 1f
    val listeners: BindableList<Player> = BindableList()

    private val msTempo = (1000.0 / nbsFile.tempo).toLong()
    private val lastTick = nbsFile.notes.keys.last()
    val timer = DockyardTimer(msTempo) {
        if (currentTick > lastTick) {
            if (looping.value) {
                reset()
                start()
                return@DockyardTimer
            }
            stop()
            return@DockyardTimer
        }
        currentTick++

        val notes = nbsFile.notes[currentTick]
        notes?.forEach { note ->
            val sound = getSound(nbsFile, note.instrument)

            val noteVolume = (nbsFile.layers[note.layer].volume * note.volume) * volume
            listeners.values.playSound(
                "minecraft:$sound",
                volume = noteVolume,
                pitch = 2f.pow((note.key - 45) / 12f),
                category = SoundCategory.RECORDS
            )
        }
    }

    init {
        looping.valueChanged {
            if (!it.newValue && isPlaying && currentTick != lastTick && !DockyardNBSConfig.enablingLoopingWhenSongIsOverStartsTheSong) return@valueChanged
            reset()
            start()
        }
    }

    fun start() {
        isPlaying = true
        timer.start()
    }

    fun stop() {
        isPlaying = false
        timer.stop()
    }

    fun seek(ticks: Int) {
        currentTick = ticks
    }

    fun reset() {
        stop()
        currentTick = nbsFile.notes.getFirst().first
        isPlaying = false
        timer.reset()
        timer.stop()
    }

    fun fadeTo(targetVolume: Float, time: Int) {
        val difference = targetVolume - volume
        val fadeFactor = difference / time

        if (difference == 0f || fadeFactor == 0f) return

        timedSequenceAsync { sequence ->
            for (i in 0 until time) {

                volume += fadeFactor
                sequence.wait(1.ticks)
            }
            volume = targetVolume
        }
    }

    fun fadeInFromZero(durationMillis: Int) {
        fadeTo(1f, durationMillis)
    }

    fun fadeOutToZero(durationMillis: Int) {
        fadeTo(0f, durationMillis)
    }

    fun dispose() {
        listeners.values.toList().forEach(listeners::remove)
        stop()
        reset()
        timer.dispose()
    }
}