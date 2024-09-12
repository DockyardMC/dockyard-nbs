package io.github.dockyardmc.nbs

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandSuggestions
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.SuggestionProvider
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.addPotionEffect
import io.github.dockyardmc.scroll.extensions.toComponent
import java.io.File

// Test Environment
fun main() {

    Events.on<ServerListPingEvent> {
        it.status.description = "<rainbow>MUSICMUSICMUSICMUSICMUSICMUSICMUSICMUSICMUSIC".toComponent()
    }

    Events.on<PlayerJoinEvent> {
        it.player.permissions.add("dockyard.*")
        it.player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 1, false)
        it.player.gameMode.value = GameMode.CREATIVE
    }

    Commands.add("/nbs") {
        addSubcommand("play") {
            addArgument("file", StringArgument(), getFilesSuggestion())
            execute {
                val player = it.getPlayerOrThrow()
                val path = getArgument<String>("file")
                val nbs = NBSParser.parse(File(path))
                player.sendMessage(nbs.toString())
            }
        }
    }

    val server = DockyardServer()
    server.start()
}


fun getFilesSuggestion(): CommandSuggestions {
    val folder = File("nbs/")
    val list: List<String> = folder.list()?.toList() ?: listOf<String>()
    return SuggestionProvider.simple(list.map { "nbs/$it" })
}