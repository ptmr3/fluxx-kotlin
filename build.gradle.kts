plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

group = "com.github.ptmr3"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api(libs.kotlinx.coroutines)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}