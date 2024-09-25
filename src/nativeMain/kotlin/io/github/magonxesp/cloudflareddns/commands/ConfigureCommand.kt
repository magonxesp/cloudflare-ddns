package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import io.github.magonxesp.cloudflareddns.services.ConfigurationService

class ConfigureCommand : CliktCommand(
    name = "configure",
    help = "Create a new configuration"
) {
    val zoneId by option().prompt("Cloudflare Zone ID")
    val apiToken by option().prompt("Cloudflare API Token")
    val hostNames by option().prompt("Host names will be updated (separated by comma)")

    override fun run() {
        val configurationService = ConfigurationService()
		configurationService.configure(zoneId, apiToken, hostNames)
    }
}
