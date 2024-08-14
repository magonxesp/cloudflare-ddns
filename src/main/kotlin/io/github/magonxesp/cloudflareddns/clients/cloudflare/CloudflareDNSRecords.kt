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
    val proxied: Boolean? = null,
    val type: String,
    val comment: String? = null,
    @SerialName("created_on")
    val createdOn: String,
    val id: String,
    val locked: Boolean? = null,
    @SerialName("modified_on")
    val modifiedOn: String,
    val proxiable: Boolean,
    val tags: List<String>? = null,
    val ttl: Int? = null,
    @SerialName("zone_id")
    val zoneId: String? = null,
    @SerialName("zone_name")
    val zoneName: String
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
