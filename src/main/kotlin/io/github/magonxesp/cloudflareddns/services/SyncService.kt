package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.clients.cloudflare.CloudflareClient
import io.github.magonxesp.cloudflareddns.clients.ipify.IpifyClient
import io.github.magonxesp.cloudflareddns.logger

class SyncService(
	private val ipifyClient: IpifyClient,
	private val cloudflareClient: CloudflareClient,
	private val configuration: Configuration
) {

	suspend fun sync() {
		logger.info("Updating hostnames ip address")

		val currentIpAddress = ipifyClient.fetchCurrentPublicIpAddress()
		logger.info("Current ip address: $currentIpAddress")
		logger.info("⚠\uFE0F Hostnames will be updated to ip address $currentIpAddress ⚠\uFE0F")

		val records = cloudflareClient.getDNSRecords().filter {
			configuration.hostNames.contains(it.name)
		}

		for (record in records) {
			cloudflareClient.updateHostName(record, currentIpAddress)
			logger.info("\uD83D\uDFE2 Hostname ${record.name} updated!")
		}

		logger.info("\u2728 Done! All hostnames are updated to Cloudflare! \u2728")
	}
}
