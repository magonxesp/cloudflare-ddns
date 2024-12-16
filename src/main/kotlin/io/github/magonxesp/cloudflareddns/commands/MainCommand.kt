package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import io.github.magonxesp.cloudflareddns.configureLogger

class MainCommand : ApplicationCommand(name = "cloudflare-ddns") {
	override fun commandHelp(context: Context): String = "Update IP address to DNS records of Cloudflare"

    init {
        subcommands(
			ConfigureCommand(),
			SyncCommand()
		)
    }

    override fun run() {
		configureLogger(
			output = logsOutput,
			logsDir = logsDir,
			json = jsonLogs
		)
	}
}
