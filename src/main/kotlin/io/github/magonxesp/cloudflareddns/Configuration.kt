package io.github.magonxesp.cloudflareddns

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

        val configDir = Path(System.getProperty("user.home"), ".cloudflare-ddns").toFile()
        val configFile = Path(configDir.absolutePath, "settings.json").toFile()

        fun read(): Configuration {
            if (!configFile.exists()) {
                error("Configuration file at ${configFile.absolutePath} not found!, " +
                        "use the \"godaddyddns configure\" command for create a new one")
            }

            val json = configFile.readText()
            return jsonEncoder.decodeFromString<Configuration>(json)
        }
    }

    fun save() {
        val json = jsonEncoder.encodeToString(this)
        configDir.mkdirs()
        configFile.writeText(json)
    }
}
