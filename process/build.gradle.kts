plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.serialization")
    idea
}


val ktor_version = "2.0.2"
val logback_version = "1.2.11"

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    //公共包
    implementation(project(":common"))
    //kotlin
    implementation(kotlin("stdlib"))

    implementation("org.koin:koin-core:1.0.2")
    implementation("org.slf4j:jcl-over-slf4j:1.7.20")
    implementation("ch.qos.logback:logback-classic:1.1.7")

    //redis web test
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    //hutool
    implementation("cn.hutool:hutool-all:5.5.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    //kotson
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation(kotlin("stdlib-jdk8"))

    //WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

}

kotlin {
    sourceSets.main {
    }
}

idea {
    module {
        // Not using += due to https://github.com/gradle/gradle/issues/8749
    }
}