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
	val content: String,
	val name: String,
	val type: String,
	val id: String,
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
