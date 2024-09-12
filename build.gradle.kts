plugins {
    kotlin("jvm") version "1.9.23"
}

group = "io.github.dockyardmc.nbs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://mvn.devos.one/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.dockyardmc:dockyard:0.6-SNAPSHOT")
    implementation("io.github.dockyardmc:scroll:1.8")
    implementation("cz.lukynka:kotlin-bindables:1.1")
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-network:2.3.10")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}