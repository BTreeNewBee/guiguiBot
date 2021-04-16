import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
//	id("io.vertx.vertx-plugin") version "0.3.1"
	kotlin("jvm") version "1.4.10"
	kotlin("plugin.spring") version "1.4.10"
}

allOpen {
}

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val kotlinVersion: String = "1.0.4"

val miraiVersion: String = "2.5.0"

val vertxVersion = "3.9.0"

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	jcenter()
	google()
	maven { url = uri ("https://dl.bintray.com/kotlin/kotlin-eap")}
}




dependencies {

//	implementation(kotlin("stdlib-jdk8"))
//	implementation("io.vertx:vertx-core")
//	implementation("io.vertx:vertx-lang-kotlin")
	implementation("org.koin:koin-core:1.0.2")
	implementation("org.slf4j:jcl-over-slf4j:1.7.20")
	implementation("ch.qos.logback:logback-classic:1.1.7")
	implementation("com.baomidou:mybatis-plus-boot-starter:3.4.1")
	implementation("com.baomidou:mybatis-plus-generator:3.4.1")
	implementation("org.apache.velocity:velocity-engine-core:2.2")
	implementation("org.freemarker:freemarker:2.3.30")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("io.netty:netty-all:4.1.29.Final")
	implementation("com.baidu.aip:java-sdk:4.15.3")
	implementation("cn.hutool:hutool-all:5.5.8")
	implementation("com.squareup:gifencoder:0.10.1")

	implementation("net.mamoe:mirai-core-jvm:$miraiVersion") {
		exclude("net.mamoe","mirai-core-api")
		exclude("net.mamoe","mirai-core-utils")
	}
	implementation("net.mamoe:mirai-core-api-jvm:$miraiVersion") {
		exclude("net.mamoe", "mirai-core-utils")
	}
	implementation("net.mamoe:mirai-core-utils-jvm:$miraiVersion")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
}


//vertx {
//	mainVerticle = "io.vertx.koin.example.BootstrapVerticle"
//}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}


tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}


