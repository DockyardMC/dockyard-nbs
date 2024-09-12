package io.github.dockyardmc.nbs

import io.github.dockyardmc.sounds.Sound
import io.netty.buffer.ByteBuf


data class NBSFile(
    val version: Int,
    val instrumentCount: Int,
    val length: Int,
    val layerCount: Int,
    val songName: String,
    val author: String,
    val originalAuthor: String,
    val description: String,
    val tempo: Double,
    val timeSignature: Int,
    val midiFileName: String,
    val loop: Boolean,
    val maxLoopCount: Int,
    val loopStart: Int,
    val ticks: Map<Int, List<Sound>>
) {
}

class Note(
    val instrument: Int,
    val key: Int,
    val volume: Int,
)

fun ByteBuf.readNote(): Note {
    val instrument = this.readByte().toInt()
    val key = this.readByte().toInt()
    val volume = this.readByte().toInt()
    this.readByte() //TODO pan
    this.readNbsShort() //TODO pitch
    return Note(instrument, key, volume)
}

fun ByteBuf.readNbsFile(): NBSFile {

    this.readNbsShort() // first two are empty
    val version = this.readByte().toInt()
    val instrumentCount = this.readByte().toInt()
    val length = this.readUnsignedShort()
    val layerCount = this.readNbsShort()
    val songName = this.readNbsString()
    val author = this.readNbsString()
    val originalAuthor = this.readNbsString()
    val description = this.readNbsString()
    val tempo = this.readNbsShort() / 100.0
    this.readByte() // auto saving
    this.readByte() // auto saving duration
    val timeSignature = this.readByte().toInt()
    this.readIntLE() // minutes spent
    this.readIntLE() // left clicks
    this.readIntLE() // right clicks
    this.readIntLE() // note blocks added
    this.readIntLE() // note blocks removed
    val midiFileName = this.readNbsString()
    val loop = this.readByte() == 1.toByte()
    val maxLoopCount = this.readByte().toInt()
    val loopStart = this.readNbsShort()

    //TODO timing points

    return NBSFile(version, instrumentCount, length, layerCount, songName, author, originalAuthor, description, tempo, timeSignature, midiFileName, loop, maxLoopCount, loopStart, mutableMapOf())
}

var sounds: List<String> = listOf(
    "block_note_block_harp",
    "block_note_block_bass",
    "block_note_block_basedrum",
    "block_note_block_snare",
    "block_note_block_hat",
    "block_note_block_guitar",
    "block_note_block_flute",
    "block_note_block_bell",
    "block_note_block_chime",
    "block_note_block_xylophone",
    "block_note_block_iron_xylophone",
    "block_note_block_cow_bell",
    "block_note_block_didgeridoo",
    "block_note_block_bit",
    "block_note_block_banjo",
    "block_note_block_pling"
)