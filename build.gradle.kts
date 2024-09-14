plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.23"
}

group = "io.github.dockyardmc.nbs"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://mvn.devos.one/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.github.dockyardmc:dockyard:0.5.3-SNAPSHOT")
    compileOnly("io.github.dockyardmc:scroll:1.8")
    compileOnly("cz.lukynka:kotlin-bindables:1.1")
    compileOnly("cz.lukynka:pretty-log:1.4")
    compileOnly("io.ktor:ktor-server-netty:2.3.10")
    compileOnly("io.ktor:ktor-network:2.3.10")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        maven {
            url = if(version.toString().endsWith("-SNAPSHOT")) {
                uri("https://mvn.devos.one/snapshots")
            } else {
                uri("https://mvn.devos.one/releases")
            }
            credentials {
                username = System.getenv()["MAVEN_USER"]
                password = System.getenv()["MAVEN_PASS"]
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            groupId = "io.github.dockyardmc"
            artifactId = "dockyard-nbs"
            version = version.toString()
            from(components["java"])
        }
    }
}