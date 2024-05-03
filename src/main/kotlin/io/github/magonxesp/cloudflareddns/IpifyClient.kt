package io.github.magonxesp.cloudflareddns

import okhttp3.OkHttpClient
import okhttp3.Request

class IpifyClient {
    private val httpClient = OkHttpClient()

    fun fetchCurrentPublicIpAddress(): String {
        val request = Request.Builder().apply {
            url("https://api.ipify.org")
        }.build()

        val response = httpClient.newCall(request).execute()
        return response.body.string()
    }
}