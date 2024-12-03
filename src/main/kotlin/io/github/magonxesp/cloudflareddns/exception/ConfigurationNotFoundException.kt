package io.github.magonxesp.cloudflareddns.exception

class ConfigurationNotFoundException(override val message: String?) : Exception(message) {
	companion object {
		fun forFileName(fileName: String) = ConfigurationNotFoundException(
			"Configuration file at $fileName not found!, " +
			"use the \"cloudflare-ddns configure\" command for create a new one"
		)
	}
}
