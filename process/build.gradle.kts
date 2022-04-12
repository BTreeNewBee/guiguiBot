plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.serialization")
}

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
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
    implementation("org.slf4j:jcl-over-slf4j:1.7.20")
    implementation("ch.qos.logback:logback-classic:1.1.7")
    //mybatis 生成器
    implementation("com.baomidou:mybatis-plus-boot-starter:3.4.1")
    implementation("com.baomidou:mybatis-plus-generator:3.4.1")
    implementation("org.apache.velocity:velocity-engine-core:2.2")
    implementation("org.freemarker:freemarker:2.3.30")

    //redis web test
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    //百度API
    implementation("com.baidu.aip:java-sdk:4.15.3")
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
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

}