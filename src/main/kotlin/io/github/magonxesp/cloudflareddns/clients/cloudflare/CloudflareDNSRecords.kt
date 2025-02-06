package io.github.magonxesp.cloudflareddns.clients.cloudflare

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DNSRecordsResponse(
	val success: Boolean,
	val result: List<DNSRecord>,
	@SerialName("result_info")
    val resultInfo: ResultInfo
)

@Serializable
data class DNSRecord(
	val comment: String,
	val content: String,
	val name: String,
	val proxied: Boolean,
	val settings: DNSRecordSettings,
	val tags: List<String>,
	val ttl: Int,
	val type: String,
	val id: String,
	val proxiable: Boolean
)

@Serializable
data class DNSRecordSettings(
	@SerialName("ipv4_only")
	val ipv4Only: Boolean,
	@SerialName("ipv6_only")
	val ipv6Only: Boolean
)

@Serializable
data class ResultInfo(
    val count: Int,
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total_count")
    val totalCount: Int
)

@Serializable
data class DNSUpdateRequest(
	@SerialName("content")
	val ipAddress: String
)
