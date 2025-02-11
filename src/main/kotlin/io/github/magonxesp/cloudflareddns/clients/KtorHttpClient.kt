package io.github.magonxesp.cloudflareddns.clients

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val httpClient get() = HttpClient {
	install(HttpRequestRetry) {
		retryOnServerErrors(maxRetries = 3)
		exponentialDelay()
	}

	install(Logging) {
		logger = Logger.DEFAULT
		level = LogLevel.HEADERS
	}

	install(ContentNegotiation) {
		json(Json {
			ignoreUnknownKeys = true
			encodeDefaults = false
		})
	}

	expectSuccess = true
}
