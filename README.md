# 🎶 Dockyard NBS Library🎵

dockyard-nbs is a dockyardmc library that implements the [NBS file format](https://opennbs.org/nbs) and allows for playing noteblock songs in-game

## Installation

<img src="https://cdn.worldvectorlogo.com/logos/kotlin-2.svg" width="16px"></img>
**Kotlin DSL**
```kotlin
repositories {
    maven {
        name = "devOS"
        url = uri("https://mvn.devos.one/releases")
    }
}

dependencies {
    implementation("io.github.dockyardmc:dockyard-nbs:1.0")
}
```


## Usage

You can parse the nbs file either using `NBSFile.fromFile(file)` or `NBSReader.read(file)`. You then get a `NBSTrack` class and you can then add a "listener" to it and use the `start()` method to play it

```kotlin
val track = NBSReader.read(File("./nbs/bad_apple.nbs"))
track.listeners.add(player)
track.start()
```
You can change the volume by either:
- Setting the `volume` field manually
- Using `fadeTo(volume, time)`, `fadeFromZero(time)`, `fadeToZero(time)` functions which interpolate the volume over time

You can control the playback of the track by using:
- `stop()` to stop the playback
- `start()` to start/resume playback
- `seek(time)` to skip
- `reset()` to stop the track and reset it to initial state
- `dispose()` to get dispose the track and the timer

You can also change the `looping` field to enable/disable automatic looping