import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.30-compose-20190503"
}

group = "me.tatarka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI("file://$projectDir/repo")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    api("org.jetbrains.kotlin:kotlin-plugin")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}