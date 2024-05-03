package io.github.magonxesp.cloudflareddns

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
    val proxied: Boolean,
    val type: String,
    val comment: String,
    @SerialName("created_on")
    val createdOn: String,
    val id: String,
    val locked: Boolean,
    val meta: Meta,
    @SerialName("modified_on")
    val modifiedOn: String,
    val proxiable: Boolean,
    val tags: List<String>,
    val ttl: Int,
    @SerialName("zone_id")
    val zoneId: String,
    @SerialName("zone_name")
    val zoneName: String
)

@Serializable
data class Meta(
    @SerialName("auto_added")
    val autoAdded: Boolean,
    val source: String
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
