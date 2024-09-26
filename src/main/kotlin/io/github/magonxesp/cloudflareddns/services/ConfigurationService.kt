package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.logger

class ConfigurationService {
	fun configure(
		zoneId: String,
		apiToken: String,
		hostNames: String
	) {
		val configuration = Configuration(
			zoneId = zoneId,
			apiToken = apiToken,
			hostNames = hostNames.trim().split(",").map { it.trim() }
		)

		configuration.save()
		logger.info("\u2728 Done! Configuration saved! \u2728")
		logger.info("Saved in ${Configuration.configFile}")
	}
}
