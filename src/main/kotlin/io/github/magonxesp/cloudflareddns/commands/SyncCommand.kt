package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.Context
import io.github.magonxesp.cloudflareddns.clients.httpClient
import io.github.magonxesp.cloudflareddns.exception.ConfigurationNotFoundException
import io.github.magonxesp.cloudflareddns.services.ConfigurationService
import io.github.magonxesp.cloudflareddns.services.SyncService

class SyncCommand : ApplicationCommand(name = "sync") {
	override fun commandHelp(context: Context) = "Update the current public IP address to the configured Cloudflare's hostnames"

    override fun run() {
		echo("\uD83D\uDC49\uFE0F Updating hostnames ip address")
		val syncService = SyncService(ConfigurationService(configFile))

		syncService.sync().onSuccess {
			echo("\uD83D\uDFE2 Done! All hostnames are updated to Cloudflare!")
		}.onFailure {
			when (it) {
				is ConfigurationNotFoundException -> echo("\uD83D\uDE31 The configuration file is missing, try to run cloudflare-ddns configure")
				else -> echo("\uD83D\uDE31 Hostnames update process has been failed! Check out the logs for more information")
			}
		}

		httpClient.close()
    }
}
