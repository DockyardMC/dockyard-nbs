package io.github.dockyardmc.nbs

import io.github.dockyardmc.extentions.toByteBuf
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

object NBSParser {

    fun parse(file: File): NBSFile {
        if(!file.exists()) throw IllegalArgumentException("File ${file.path} does not exist!")
        if(file.extension != "nbs") throw IllegalArgumentException("File ${file.path} is not of type nbs!")

        try {
            val nbs = file.readBytes().toByteBuf().readNbsFile()
            return nbs
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IllegalStateException("There was an error while reading the nbs file: $ex")
        }
    }
}