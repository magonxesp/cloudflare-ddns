package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import io.github.magonxesp.cloudflareddns.services.ConfigurationService

class ConfigureCommand : ApplicationCommand(name = "configure",) {
	override fun commandHelp(context: Context) = "Create a new configuration"

    private val zoneId by option().prompt("\uD83D\uDC49\uFE0F Cloudflare Zone ID")
	private val apiToken by option().prompt("\uD83D\uDC49\uFE0F Cloudflare API Token")
	private val hostNames by option().prompt("\uD83D\uDC49\uFE0F Host names will be updated (separated by comma)")

    override fun run() {
        val configurationService = ConfigurationService(configFile)
		configurationService.configure(zoneId, apiToken, hostNames).onSuccess {
			echo("\uD83D\uDFE2 Configuration saved!")
		}.onFailure {
			echo("\uD83D\uDE31 Failed to save the configuration")
		}
    }
}
