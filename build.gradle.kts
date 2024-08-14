import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.11"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"
}

taboolib {
    env {
        // 安装模块
        install(UNIVERSAL, BUKKIT, BUKKIT_UTIL, BUKKIT_XSERIES, UI, NMS)
    }
    version { taboolib = "6.1.2-beta10" }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.mojang:authlib:1.5.25")
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
