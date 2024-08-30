import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.11"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"
}

taboolib {
    description {
        contributors {
            name("Mical")
            name("HXS")
        }
    }
    env {
        // 安装模块
        install(
            "minecraft-chat",
            "basic-configuration",
            "bukkit-util",
            "bukkit-xseries",
            "bukkit-xseries-item",
            "bukkit-xseries-skull",
            "bukkit-ui",
            "platform-bukkit"
        )
        forceDownloadInDev = false
        repoTabooLib = "http://mcstarrysky.com:8081/repository/releases/"
    }
    version { taboolib = "6.2.0-beta20-dev" }
}

repositories {
    maven("http://mcstarrysky.com:8081/repository/releases/") {
        isAllowInsecureProtocol = true
    }
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
