import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
}

group = rootProject.group
val rootDependencyDir = "${group}.dependencies"
val dependencyDir = "${group}.bungee.dependencies"
version = rootProject.version
base {
    archivesName.set("${rootProject.name}-Bungee")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("cloud.commandframework:cloud-bungee:${VersionConstants.cloudVersion}")
    implementation("net.kyori:adventure-api:${VersionConstants.adventureVersion}")
    implementation("net.kyori:adventure-platform-bungeecord:${VersionConstants.adventurePlatformVersion}")
    implementation("net.kyori:adventure-text-minimessage:${VersionConstants.adventureMinimessageVersion}") {
        exclude("net.kyori", "adventure-api")
    }
    implementation("org.bstats:bstats-bungeecord:${VersionConstants.bstatsVersion}")
    implementation(project(":Common"))
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    relocate("org.bstats", "${dependencyDir}.bstats")
}

bungee {
    name = "ServerUtils"
    main = "net.frankheijden.serverutils.bungee.ServerUtils"
    description = "A server utility"
    softDepends = setOf("ServerUtilsUpdater")
    author = "FrankHeijden"
}
