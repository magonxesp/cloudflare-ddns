package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import io.github.magonxesp.cloudflareddns.CLOUDFLAREDDNS_DIRNAME
import io.github.magonxesp.cloudflareddns.LOGS_OUTPUT_STDOUT
import io.github.magonxesp.cloudflareddns.configureLogger
import kotlin.io.path.Path

class MainCommand : CliktCommand(
    help = "Update IP address to dns records of Cloudflare"
) {
	val logsOutput by option("--log-output", help = "stdout or file, defaults to stdout").default(LOGS_OUTPUT_STDOUT)
	val logsDir by option("--log-dir", help = "The directory to place logs, by default is shown in STDOUT")
		.default(Path(System.getProperty("user.home"), CLOUDFLAREDDNS_DIRNAME, "logs").toString())
	val jsonLogs by option("--json-logs", help = "The format of the logs").flag(default = false)

    init {
        subcommands(ConfigureCommand(), SyncCommand())
    }

    override fun run() {
		configureLogger(logsOutput, logsDir, jsonLogs)
	}
}
