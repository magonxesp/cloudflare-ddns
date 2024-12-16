package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.github.magonxesp.cloudflareddns.CLOUDFLAREDDNS_DIRNAME
import io.github.magonxesp.cloudflareddns.LOGS_OUTPUT_STDOUT
import kotlin.io.path.Path

abstract class ApplicationCommand(name: String? = null) : CliktCommand(name = name) {
	protected val logsOutput by option("--log-output", help = "stdout or file, defaults to stdout")
		.default(LOGS_OUTPUT_STDOUT)
	protected val logsDir by option("--log-dir", help = "The directory to place logs, by default is shown in STDOUT")
		.default(Path(System.getProperty("user.home"), CLOUDFLAREDDNS_DIRNAME, "logs").toString())
	protected val jsonLogs by option("--json-logs", help = "The format of the logs").flag(default = false)
	protected val configFile by option("--config", "-c", help = "Path to configuration file")
}
