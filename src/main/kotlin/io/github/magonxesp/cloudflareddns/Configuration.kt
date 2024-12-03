package io.github.magonxesp.cloudflareddns

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
	val zoneId: String,
	val apiToken: String,
	val hostNames: List<String>,
)
