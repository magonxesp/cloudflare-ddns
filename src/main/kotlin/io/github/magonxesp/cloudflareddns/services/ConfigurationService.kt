package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.CLOUDFLAREDDNS_DIRNAME
import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.exception.ConfigurationNotFoundException
import io.github.magonxesp.cloudflareddns.logger
import io.github.magonxesp.cloudflareddns.runCatchingSafe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.Path

class ConfigurationService(configFilePath: String? = null) {
	private val jsonEncoder = Json {
		ignoreUnknownKeys = true
		prettyPrint = true
	}

	private val isDefinedByUser = configFilePath != null
	private val configPath = File(configFilePath ?: Path(
		System.getProperty("user.home"), CLOUDFLAREDDNS_DIRNAME, "settings.json"
	).toString())

	private val configFile: String
		get() = configPath.name

	fun configure(
		zoneId: String,
		apiToken: String,
		hostNames: String
	): Result<Unit> = runCatchingSafe {
		val configuration = Configuration(
			zoneId = zoneId,
			apiToken = apiToken,
			hostNames = hostNames.trim().split(",").map { it.trim() }
		)

		save(configuration)
	}

	fun read(): Configuration {
		if (!configPath.exists()) {
			logger.warn("The configuration file $configFile does not exist")
			throw ConfigurationNotFoundException.forFileName(configFile)
		}

		logger.info("Found configuration on: ${configPath.path}")
		val json = configPath.readText()
		return jsonEncoder.decodeFromString<Configuration>(json)
	}

	fun save(configuration: Configuration) {
		logger.info("Saving configuration on: ${configPath.path}")
		val json = jsonEncoder.encodeToString(configuration)

		if (!configPath.exists() && !isDefinedByUser) {
			configPath.mkdirs()
		}

		configPath.writeText(json)
	}
}
