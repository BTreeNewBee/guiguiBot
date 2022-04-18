import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//	id("io.vertx.vertx-plugin") version "0.3.1"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.serialization") version "1.6.10"
	id("org.springframework.boot") version "2.4.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
}



group = "com.iguigui"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val kotlinVersion: String = "1.5.30"

val miraiVersion: String = "2.10.0"

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

}


allprojects {
	repositories {
		mavenCentral()
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
		jcenter()
		google()
		maven { url = uri ("https://dl.bintray.com/kotlin/kotlin-eap")}
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")

	repositories {
		mavenCentral()
	}

	dependencies {
		implementation("net.mamoe:mirai-core-api-jvm:$miraiVersion") {
			exclude("net.mamoe", "mirai-core-utils")
		}
		implementation("org.springframework.boot:spring-boot-starter:2.6.0")
//		implementation("org.apache.dubbo:dubbo-spring-boot-starter:2.7.8")
//		implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")
		implementation("de.javakaffee:kryo-serializers:0.45")
		implementation("com.esotericsoftware:kryo:4.0.2")
	}


}



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