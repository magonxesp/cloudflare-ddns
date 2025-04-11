package io.github.magonxesp.cloudflareddns

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

const val LOGS_OUTPUT_STDOUT = "stdout"
const val LOGS_OUTPUT_FILE = "file"
private var loggerContext: LoggerContext? = null

fun ConfigurationBuilder<BuiltConfiguration>.consoleAppender(): Pair<AppenderComponentBuilder, String> =
	newAppender("CONSOLE", "Console") to "CONSOLE"

fun ConfigurationBuilder<BuiltConfiguration>.fileAppender(directory: String): Pair<AppenderComponentBuilder, String> =
	newAppender("FILE", "RollingFile")
		.addAttribute("filePattern", "$directory/cloudflare-ddns-%d{yyyy-MM-dd}.log")
		.addAttribute("fileName", "$directory/cloudflare-ddns.log")
		.addComponent(
			newComponent("Policies")
				.addComponent(
					newComponent("SizeBasedTriggeringPolicy")
						.addAttribute("size", "10MB")
				)
		) to "FILE"

fun ConfigurationBuilder<BuiltConfiguration>.jsonLayout(): LayoutComponentBuilder =
	newLayout("JsonTemplateLayout")
		.addAttribute("eventTemplateUri", "classpath:log-layout.json")

fun ConfigurationBuilder<BuiltConfiguration>.patternLayout(): LayoutComponentBuilder =
	newLayout("PatternLayout")
		.addAttribute("pattern", "[%d{ISO8601}] - %p - %c{-10}: %m%n")

fun configureLogger(output: String, logsDir: String, json: Boolean = false) {
	val builder = ConfigurationBuilderFactory.newConfigurationBuilder()
	val (appender, reference) = if (output == LOGS_OUTPUT_STDOUT) {
		builder.consoleAppender()
	} else {
		builder.fileAppender(logsDir)
	}

	val layout = if (json) {
		builder.jsonLayout()
	} else {
		builder.patternLayout()
	}

	appender.add(layout)
	builder.add(appender)
	builder.add(
		builder.newRootLogger(Level.INFO)
			.add(builder.newAppenderRef(reference))
	)

	val configuration = builder.build(false)
	loggerContext = Configurator.initialize(configuration)
}

val Any.logger: Logger
	get() = loggerContext!!.getLogger(this::class.java)
