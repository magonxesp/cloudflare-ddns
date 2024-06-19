package io.github.magonxesp.cloudflareddns.services

import io.github.magonxesp.cloudflareddns.Configuration
import io.github.magonxesp.cloudflareddns.clients.cloudflare.CloudflareClient
import io.github.magonxesp.cloudflareddns.clients.ipify.IpifyClient
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.concurrent.thread

class SyncService(
	private val ipifyClient: IpifyClient,
	private val cloudflareClient: CloudflareClient,
	private val configuration: Configuration
) {
	private val logger = LoggerFactory.getLogger(this::class.java)

	fun sync() {
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

	fun syncEveryHour() {
		var running = true

		Runtime.getRuntime().addShutdownHook(thread(start = false) {
			running = false
		})

		// first sync
		sync()

		while (running) {
			val now = Instant.now()

			if (now.epochSecond % 3600 == 0L) {
				sync()
			}
		}
	}
}
