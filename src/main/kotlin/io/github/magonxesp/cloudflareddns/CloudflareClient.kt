package io.github.magonxesp.cloudflareddns

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class CloudflareClient(
    private val zoneId: String,
    private val apiKey: String,
) {
    private val httpClient = OkHttpClient()

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonEncoder = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = false
    }

    private fun request(builder: Request.Builder.() -> Unit) = Request.Builder().apply {
        header("X-Auth-Key", apiKey)
        builder()
    }.build()


    fun getDNSRecords(): List<DNSRecord> {
        val request = request {
            url("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records")
        }

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            error("Failed get DNS records from Cloudflare: ${response.code} (${response.message}): ${response.body.string()}")
        }

        val records = jsonEncoder.decodeFromString<DNSRecordsResponse>(response.body.string())
        return records.result
    }

    fun updateHostName(dnsRecord: DNSRecord, ipAddress: String) {
        val request = request {
            url("https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records/${dnsRecord.id}")
            patch(jsonEncoder.encodeToString(mapOf("content" to ipAddress)).toRequestBody("application/json".toMediaType()))
        }

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            error("Failed to update hostname on Cloudflare: ${response.code} (${response.message}): ${response.body.string()}")
        }
    }
}