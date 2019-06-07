import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile
import java.net.URI

plugins {
    kotlin("jvm")
    application
}

group = "me.tatarka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI("file://${rootProject.projectDir}/repo")
    }
}

val conf = configurations.create("kotlinPlugin")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    add("kotlinPlugin", project(path = ":"))
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(conf)
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs += "-Xplugin=${conf.files.first()}"
    }
}

application {
    mainClassName = "me.tatarka.kotlinir.sample.Main"
}
