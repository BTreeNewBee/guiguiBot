plugins {
    kotlin("jvm")
}

group = "com.iguigui"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup:javapoet:1.12.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.20-1.0.5")
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
    }
}

//sourceSets.main {
//    java.srcDirs("src/main/kotlin")
//}