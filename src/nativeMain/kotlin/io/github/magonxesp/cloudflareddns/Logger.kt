package io.github.magonxesp.cloudflareddns

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Log {
	@Serializable
	private data class ExceptionInfo(
		val exception: String,
		val message: String,
		@SerialName("stack_trace")
		val stacktrace: String? = null
	)

	@Serializable
	private data class Output(
		val timestamp: String,
		val level: String,
		val message: String,
		val exception: ExceptionInfo? = null
	)

	private val jsonEncoder = Json

	private fun printLog(
		level: String,
		message: String,
		throwable: Throwable? = null
	) = println(jsonEncoder.encodeToString(Output(
		timestamp = Clock.System.now().toString(),
		level = level,
		message = message,
		exception = throwable?.let {
			ExceptionInfo(
				exception = it::class.toString(),
				message = it.message ?: "",
				stacktrace = it.stackTraceToString()
			)
		}
	)))

	fun debug(message: String) = printLog("DEBUG", message)
	fun info(message: String) = printLog("INFO", message)
	fun error(message: String, throwable: Throwable? = null) = printLog("ERROR", message, throwable)
}
