package io.github.dockyardmc.nbs

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.*
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.addPotionEffect
import io.github.dockyardmc.scroll.extensions.toComponent
import java.io.File

lateinit var track: NBSTrack

fun getFilesSuggestion(): CommandSuggestions {
    val folder = File("nbs/")
    val list: List<String> = folder.list()?.toList() ?: listOf()
    return SuggestionProvider.simple(list.map { "nbs/$it" })
}

// Test Environment
fun main() {

    Events.on<ServerListPingEvent> {
        it.status.description = "<rainbow>Dockyard Noteblock Stuff".toComponent()
    }

    Events.on<PlayerJoinEvent> {
        it.player.permissions.add("dockyard.*")
        it.player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 1, false)
        it.player.gameMode.value = GameMode.CREATIVE
    }

    Commands.add("/nbs") {
        addSubcommand("load") {
            addArgument("file", StringArgument(), getFilesSuggestion())
            execute {
                val player = it.getPlayerOrThrow()
                val path = getArgument<String>("file")
                val nbsFile = NBSReader.parse(File(path))
                if (::track.isInitialized) {
                    track.stop()
                    track.reset()
                    track.dispose()
                }
                track = NBSTrack(nbsFile)
                track.listeners.add(player)
                val name = nbsFile.songName.ifEmpty { path.split("/").last() }
                player.sendMessage("<lime>Loaded track <yellow>$name<lime>.. playing it now!")
                track.start()
            }
        }

        addSubcommand("play") {
            execute {
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.start()
            }
        }

        addSubcommand("stop") {
            execute {
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.stop()
            }
        }

        addSubcommand("reset") {
            execute {
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.reset()
            }
        }

        addSubcommand("dispose") {
            execute {
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.dispose()
            }
        }

        addSubcommand("volume") {
            addArgument("volume", FloatArgument())
            execute {
                val volume = getArgument<Float>("volume")
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.volume = volume
            }
        }

        addSubcommand("fade_in") {
            addArgument("time", IntArgument())
            execute {
                val time = getArgument<Int>("time")
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.fadeInFromZero(time)
            }
        }

        addSubcommand("fade_out") {
            addArgument("time", IntArgument())
            execute {
                val time = getArgument<Int>("time")
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.fadeOutToZero(time)
            }
        }

        addSubcommand("looping") {
            addArgument("looping", BooleanArgument())
            execute {
                val looping = getArgument<Boolean>("looping")
                if (!::track.isInitialized) throw CommandException("track is not loaded!")
                track.looping.value = looping
            }
        }
    }

    val server = DockyardServer()
    server.start()
}