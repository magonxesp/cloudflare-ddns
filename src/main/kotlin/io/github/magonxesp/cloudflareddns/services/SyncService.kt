package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.clients.cloudflare.CloudflareClient
import io.github.magonxesp.cloudflareddns.clients.ipify.IpifyClient
import io.github.magonxesp.cloudflareddns.logger
import io.github.magonxesp.cloudflareddns.runCatchingSafe
import kotlinx.coroutines.runBlocking

class SyncService(private val configurationService: ConfigurationService) {
	fun sync(): Result<Unit> = runCatchingSafe {
		runBlocking {
			val configuration = configurationService.read()
			val client = CloudflareClient(configuration.zoneId, configuration.apiToken)
			val currentIpAddress = IpifyClient().fetchCurrentPublicIpAddress()

			logger.info("Current ip address: $currentIpAddress")
			logger.info("Hostnames will be updated to ip address $currentIpAddress")

			val records = client.getDNSRecords().filter {
				configuration.hostNames.contains(it.name)
			}

			for (record in records) {
				client.updateHostName(record, currentIpAddress)
				logger.info("Hostname ${record.name} updated!")
			}
		}
	}
}
