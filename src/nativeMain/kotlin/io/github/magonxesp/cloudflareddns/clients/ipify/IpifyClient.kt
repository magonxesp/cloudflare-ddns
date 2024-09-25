package io.github.magonxesp.cloudflareddns.clients.ipify

import io.github.magonxesp.cloudflareddns.clients.httpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class IpifyClient {
    suspend fun fetchCurrentPublicIpAddress(): String {
        val response = httpClient.get("https://api.ipify.org") {
			contentType(ContentType.Text.Plain)
			accept(ContentType.Text.Plain)
		}

        return response.bodyAsText()
    }
}
