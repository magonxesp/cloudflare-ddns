package io.github.magonxesp.cloudflareddns.clients.cloudflare

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class CloudflareClient(
	private val zoneId: String,
	private val apiToken: String,
) {
    private val httpClient = OkHttpClient()

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonEncoder = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = false
    }

    fun getDNSRecords(): List<DNSRecord> {
        val request = Request.Builder().apply {
            url("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records")
			header("Authorization", "Bearer $apiToken")
        }.build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            error("Failed get DNS records from Cloudflare: ${response.code} (${response.message}): ${response.body.string()}")
        }

        val records = jsonEncoder.decodeFromString<DNSRecordsResponse>(response.body.string())
        return records.result
    }

    fun updateHostName(dnsRecord: DNSRecord, ipAddress: String) {
        val request = Request.Builder().apply {
            url("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records/${dnsRecord.id}")
            patch(jsonEncoder.encodeToString(mapOf("content" to ipAddress)).toRequestBody("application/json".toMediaType()))
			header("Authorization", "Bearer $apiToken")
        }.build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            error("Failed to update hostname on Cloudflare: ${response.code} (${response.message}): ${response.body.string()}")
        }
    }
}
