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
    maven { url = uri("https://repository.apache.org/snapshots") }
}


configurations {
    all {
        exclude("org.springframework.boot","spring-boot-starter-logging")
    }
}

dependencies {
    //公共包
    implementation(project(":common"))
    //kotlin
    implementation(kotlin("stdlib"))
    //mysql
    runtimeOnly("mysql:mysql-connector-java:8.0.28")
    //网易云client
    implementation("top.yumbo.music:yumbo-music-utils:1.2.3")

    implementation("org.koin:koin-core:1.0.2")
    //mybatis 生成器
    implementation("com.baomidou:mybatis-plus-boot-starter:3.5.3.1")
    implementation("com.baomidou:mybatis-plus-generator:3.4.1")
    implementation("org.apache.velocity:velocity-engine-core:2.2")
    implementation("org.freemarker:freemarker:2.3.30")

    //redis web test
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    //spring-boot-starter-log4j2
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    //百度API
//    implementation("com.baidu.aip:java-sdk:4.15.3")
    //hutool
    implementation("cn.hutool:hutool-all:5.5.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    //netty
    implementation("io.netty:netty-all:4.1.54.Final")
    //rabbit mq
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    //kotson
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation(kotlin("stdlib-jdk8"))

    //WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    //mongodb
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    //opencc4j 简繁体转换
    implementation("com.github.houbb:opencc4j:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    //image process
    implementation("io.github.fanyong920:jvppeteer:1.1.5")
    implementation("org.freemarker:freemarker:2.3.31")
    //JCommander
    implementation("com.beust:jcommander:1.82")
    //kotlin log4j
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.3.0-SNAPSHOT")
//    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

idea {
    module {
        // Not using += due to https://github.com/gradle/gradle/issues/8749
        sourceDirs = sourceDirs + file("build/generated/ksp/main/kotlin") // or tasks["kspKotlin"].destination
        testSourceDirs = testSourceDirs + file("build/generated/ksp/test/kotlin")
        generatedSourceDirs = generatedSourceDirs + file("build/generated/ksp/main/kotlin") + file("build/generated/ksp/test/kotlin")
    }
}