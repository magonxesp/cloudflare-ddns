package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.Log

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
		Log.info("\u2728 Done! Configuration saved! \u2728")
		Log.info("Saved in ${Configuration.configFile}")
	}
}
