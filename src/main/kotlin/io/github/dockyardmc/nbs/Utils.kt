package io.github.dockyardmc.nbs

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset

fun ByteBuf.readNbsString(): String {
    val size = this.readIntLE()
    val textBytes = this.readBytes(size)
    val text = textBytes.toString(Charset.defaultCharset())
    return text
}

fun ByteBuf.readNbsShort(): Int {
    val bytes = ByteArray(2)
    this.readBytes(bytes)

    var sum = 0
    var i = 0
    for (b in bytes) {
        sum += java.lang.Byte.toUnsignedInt(b) shl (i++ * 8)
    }

    return sum
}