package io.github.dockyardmc.nbs

import io.netty.buffer.ByteBuf
import java.io.File

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
    val notes: Map<Int, List<Note>>,
    val layers: List<Layer>,
    val customInstruments: List<String>,
) {

    companion object {
        fun fromFile(file: File): NBSFile {
            return NBSReader.parse(file)
        }
    }

    override fun toString(): String {
        return "NbsFile(version=$version, song=$songName, author=$author, tempo=$tempo, notes=${notes.size}, layers=${layers.size})"
    }
}

data class Layer(
    val name: String,
    val locked: Boolean,
    val volume: Float,
    val pan: Float,
)

data class Note(
    val instrument: Int,
    val key: Int,
    val volume: Float,
    val layer: Int,
    val pan: Float,
)

fun ByteBuf.readNote(layer: Int): Note {
    val instrument = this.readByte().toInt()
    val key = this.readByte().toInt()
    val volume = this.readByte().toInt()
    val pan = this.readByte()
    this.readNbsShort() // pitch
    return Note(instrument, key, volume / 100f, layer, pan / 100f)
}

fun ByteBuf.readNbsFile(): NBSFile {

    val shouldBeEmpty = this.readNbsShort() // first two are empty
    if(shouldBeEmpty != 0) throw IllegalStateException("Old NBS format is unsupported. Please load this song in Open Noteblock Studio and save it again to get the new file format!")
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

    val timingPoints = readTimingPoints(this)
    val layers = readLayers(this, layerCount)
    val customInstruments = readCustomInstruments(this)

    return NBSFile(
        version = version,
        instrumentCount = instrumentCount,
        length = length,
        layerCount = layerCount,
        songName = songName,
        author = author,
        originalAuthor = originalAuthor,
        description = description,
        tempo = tempo,
        timeSignature = timeSignature,
        midiFileName = midiFileName,
        loop = loop,
        maxLoopCount = maxLoopCount,
        loopStart = loopStart,
        notes = timingPoints,
        layers = layers,
        customInstruments = customInstruments
    )
}

fun readTimingPoints(buf: ByteBuf): Map<Int, List<Note>> {
    val timingPoints = mutableMapOf<Int, List<Note>>()
    var i = 0
    while (true) {
        val jumps = buf.readNbsShort()
        if (jumps == 0) break
        i += jumps

        val timingPoint = readNotes(buf)
        timingPoints[i] = timingPoint
    }

    return timingPoints
}

fun readNotes(buf: ByteBuf): List<Note> {
    val notes = mutableListOf<Note>()

    while (true) {
        val layer = buf.readNbsShort()
        if (layer == 0) break

        notes.add(buf.readNote(layer))
    }

    return notes
}

fun readLayers(buf: ByteBuf, layerCount: Int): MutableList<Layer> {
    val layers = mutableListOf<Layer>()
    for (i in 0 until layerCount) {
        val name = buf.readNbsString()
        val lock = buf.readByte()
        val volume = buf.readByte()
        val pan = buf.readByte()
        layers.add(Layer(name, lock.toInt() == 1, volume / 100f, pan / 100f))
    }
    return layers
}

fun readCustomInstruments(buf: ByteBuf): List<String> {
    val instruments = buf.readByte().toInt()
    val customInstruments = mutableListOf<String>()
    if (instruments > 240) throw IllegalStateException("More than maximum amount of instruments found in your nbs file! (>240)")
    for (i in 0 until instruments) {
        buf.readNbsString() // name
        val file = buf.readNbsString().replace(".ogg", "").split("/").last()
        buf.readByte() // key
        buf.readByte() // should press
        customInstruments.add(file)
    }
    return customInstruments
}

private var sounds: List<String> = listOf(
    "block.note_block.harp",
    "block.note_block.bass",
    "block.note_block.basedrum",
    "block.note_block.snare",
    "block.note_block.hat",
    "block.note_block.guitar",
    "block.note_block.flute",
    "block.note_block.bell",
    "block.note_block.chime",
    "block.note_block.xylophone",
    "block.note_block.iron_xylophone",
    "block.note_block.cow_bell",
    "block.note_block.didgeridoo",
    "block.note_block.bit",
    "block.note_block.banjo",
    "block.note_block.pling",
)

fun getSound(file: NBSFile, index: Int): String {
    val combined = sounds.toMutableList()
    combined.addAll(file.customInstruments)
    val sound = combined.getOrNull(index) ?: throw IllegalStateException("Instrument with index $index not found!")
    return sound
}