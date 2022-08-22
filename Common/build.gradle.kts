import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.kyori.blossom") version "1.3.0"
}

group = rootProject.group
version = "${rootProject.version}"
base {
    archivesName.set("${rootProject.name}-Common")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("net.kyori:adventure-platform-api:${VersionConstants.adventurePlatformVersion}") {
        exclude("net.kyori", "adventure-api")
        exclude("net.kyori", "adventure-text-minimessage")
    }
    compileOnly("net.kyori:adventure-text-minimessage:${VersionConstants.adventureMinimessageVersion}")
    compileOnly("com.github.FrankHeijden:ServerUtilsUpdater:5f722b10d1")

    testImplementation("net.kyori:adventure-text-serializer-plain:${VersionConstants.adventureVersion}")
}

tasks {
    blossom {
        replaceToken("{version}", version, "src/main/java/net/frankheijden/serverutils/common/ServerUtilsApp.java")
    }
}

tasks.withType<ShadowJar> {
    exclude("plugin.yml")
    exclude("bungee.yml")
}
