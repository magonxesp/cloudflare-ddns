import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	application
}

group = "io.github.magonxesp"
version = "0.1.4"

repositories {
    mavenCentral()
}

dependencies {
	val ktor_version = "2.3.12"
	val log4j_version = "2.24.0"

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
	implementation("com.github.ajalt.clikt:clikt:4.2.2")
	implementation("io.ktor:ktor-client-core:$ktor_version")
	implementation("io.ktor:ktor-client-okhttp:$ktor_version")
	implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
	implementation("io.ktor:ktor-client-logging:$ktor_version")
	implementation("org.apache.logging.log4j:log4j-api:$log4j_version")
	runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:$log4j_version")
	runtimeOnly("org.apache.logging.log4j:log4j-core:$log4j_version")
	implementation("org.apache.logging.log4j:log4j-layout-template-json:$log4j_version")
}

application {
	mainClass.set("io.github.magonxesp.cloudflareddns.MainKt")
}

tasks.withType<ShadowJar> {
	archiveFileName = "cloudflare-ddns.jar"
	transform(Log4j2PluginsCacheFileTransformer())
}
