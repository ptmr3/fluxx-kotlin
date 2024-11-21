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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}