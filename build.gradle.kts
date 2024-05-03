plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "io.github.magonxesp"
version = "0.0.5"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.github.ajalt.clikt:clikt:4.2.2")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "io.github.magonxesp.cloudflareddns.MainKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("cloudflareddns")
            mainClass.set("io.github.magonxesp.cloudflareddns.MainKt")
        }
    }
    binaries.all {
        buildArgs.add("--verbose")
    }

    toolchainDetection.set(true)
}
