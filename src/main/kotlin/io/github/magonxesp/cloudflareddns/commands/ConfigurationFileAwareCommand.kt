package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option

abstract class ConfigurationFileAwareCommand(
	name: String,
	help: String
) : CliktCommand(
	name = name,
	help = help
) {
	protected val configFile by option("--config", "-c", help = "Path to configuration file")
}
