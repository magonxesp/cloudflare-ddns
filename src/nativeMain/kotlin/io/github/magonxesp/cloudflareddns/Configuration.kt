package io.github.magonxesp.cloudflareddns

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath

@Serializable
data class Configuration(
	val zoneId: String,
	val apiToken: String,
	val hostNames: List<String>,
) {
    companion object {
        private val jsonEncoder = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        val configDir = path(homeDir, ".cloudflare-ddns").toPath()
        val configFile = path(configDir.toString(), "settings.json").toPath()

        fun read(): Configuration {
            if (!FileSystem.SYSTEM.exists(configFile)) {
                error("Configuration file at $configFile not found!, " +
                        "use the \"godaddyddns configure\" command for create a new one")
            }

            val json = FileSystem.SYSTEM.read(configFile) { readUtf8() }
            return jsonEncoder.decodeFromString<Configuration>(json)
        }
    }

    fun save() {
        val json = jsonEncoder.encodeToString(this)

		if (!FileSystem.SYSTEM.exists(configDir)) {
			FileSystem.SYSTEM.createDirectories(configDir)
		}

		FileSystem.SYSTEM.write(configFile) {
			writeUtf8(json)
		}
    }
}
