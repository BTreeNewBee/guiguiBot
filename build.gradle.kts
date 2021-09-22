import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
//	id("io.vertx.vertx-plugin") version "0.3.1"
	kotlin("jvm") version "1.5.30"
	kotlin("plugin.spring") version "1.5.30"
}



group = "com.iguigui"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val kotlinVersion: String = "1.5.30"

val miraiVersion: String = "2.7.0"

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
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.baidu.aip:java-sdk:4.15.3")
	implementation("cn.hutool:hutool-all:5.5.8")
	implementation("com.squareup:gifencoder:0.10.1")

	val miraiVersion = "2.7.0"
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

	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
	implementation(kotlin("stdlib-jdk8"))
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


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}


val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}