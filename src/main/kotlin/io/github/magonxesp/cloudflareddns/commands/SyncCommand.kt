package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.github.magonxesp.cloudflareddns.clients.cloudflare.CloudflareClient
import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.clients.ipify.IpifyClient
import io.github.magonxesp.cloudflareddns.services.SyncService

class SyncCommand : CliktCommand(
    name = "sync",
    help = "Update the current public IP address to the configured Cloudflare's hostnames"
) {
	val keepSyncing by option("-k", "--keep-syncing", help = "Keep syncing every hour").flag(default = false)

    override fun run() {
        val configuration = Configuration.read()
        val ipifyClient = IpifyClient()
        val cloudflareClient = CloudflareClient(configuration.zoneId, configuration.apiToken)
		val syncService = SyncService(ipifyClient, cloudflareClient, configuration)

		if (keepSyncing) {
			syncService.syncEveryHour()
		} else {
			syncService.sync()
		}
    }
}
