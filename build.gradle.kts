plugins {
    kotlin("multiplatform") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

group = "io.github.magonxesp"
version = "0.1.4"

repositories {
    mavenCentral()
}

kotlin {
	jvmToolchain(21)

	linuxX64 {
		binaries {
			executable {
				entryPoint = "io.github.magonxesp.cloudflareddns.main"
			}
		}
	}
	mingwX64 {
		binaries {
			executable {
				entryPoint = "io.github.magonxesp.cloudflareddns.main"
			}
		}
	}

	sourceSets {
		nativeMain {
			dependencies {
				val ktor_version = "2.3.12"

				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
				implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
				implementation("com.github.ajalt.clikt:clikt:4.2.2")
				implementation("io.ktor:ktor-client-core:$ktor_version")
				implementation("io.ktor:ktor-client-curl:$ktor_version")
				implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
				implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
				implementation("com.squareup.okio:okio:3.9.1")
			}
		}
	}
}
