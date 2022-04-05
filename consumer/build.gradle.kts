plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(kotlin("stdlib"))
    runtimeOnly("mysql:mysql-connector-java:8.0.28")
    implementation("top.yumbo.music:yumbo-music-utils:1.2.3")
//    implementation("com.gitee.reger:spring-boot-starter-dubbo:1.1.3")
//    implementation("org.apache.curator:curator-framework:2.12.0")
}