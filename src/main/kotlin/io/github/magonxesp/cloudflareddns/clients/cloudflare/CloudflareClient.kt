package io.github.magonxesp.cloudflareddns.clients.cloudflare

import io.github.magonxesp.cloudflareddns.clients.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class CloudflareClient(
	private val zoneId: String,
	private val apiToken: String,
) {
	suspend fun getDNSRecords(): List<DNSRecord> = httpClient.use { client ->
		val response = client.get("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records") {
			contentType(ContentType.Application.Json)
			accept(ContentType.Application.Json)
			bearerAuth(apiToken)
		}

		val records = response.body<DNSRecordsResponse>()
		return records.result
	}

	suspend fun updateHostName(dnsRecord: DNSRecord, ipAddress: String) = httpClient.use { client ->
		client.patch("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records/${dnsRecord.id}") {
			contentType(ContentType.Application.Json)
			accept(ContentType.Application.Json)
			bearerAuth(apiToken)
			setBody(DNSUpdateRequest(ipAddress = ipAddress))
		}
	}
}

