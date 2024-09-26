package io.github.magonxesp.cloudflareddns

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.io.path.Path

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

		val homeDir = System.getProperty("user.home")
        val configDir = Path(homeDir, ".cloudflare-ddns").toFile()
        val configFile = Path(configDir.toString(), "settings.json").toFile()

        fun read(): Configuration {
            if (!configFile.exists()) {
                error("Configuration file at $configFile not found!, " +
                        "use the \"cloudflare-ddns configure\" command for create a new one")
            }

            val json = configFile.readText()
            return jsonEncoder.decodeFromString<Configuration>(json)
        }
    }

    fun save() {
        val json = jsonEncoder.encodeToString(this)

		if (!configDir.exists()) {
			configDir.mkdirs()
		}

		configFile.writeText(json)
    }
}
