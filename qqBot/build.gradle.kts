plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
//	id("io.vertx.vertx-plugin") version "0.3.1"
}

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"


val kotlinVersion: String = "1.5.30"

val miraiVersion: String = "2.10.0"


repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    jcenter()
    google()
    maven { url = uri ("https://dl.bintray.com/kotlin/kotlin-eap")}
}
dependencies {
    implementation("org.koin:koin-core:1.0.2")
    implementation("ch.qos.logback:logback-classic:1.1.7")
//    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("net.mamoe:mirai-core-jvm:$miraiVersion") {
        exclude("net.mamoe","mirai-core-api")
        exclude("net.mamoe","mirai-core-utils")
    }
    implementation("net.mamoe:mirai-core-api-jvm:$miraiVersion") {
        exclude("net.mamoe", "mirai-core-utils")
    }
    implementation("net.mamoe:mirai-core-utils-jvm:$miraiVersion")
    //强制指定版本号，不知道哪里版本冲突了很生气
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation(kotlin("stdlib-jdk8"))
    //公共包
    implementation(project(":common"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}