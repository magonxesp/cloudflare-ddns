package io.github.magonxesp.cloudflareddns

fun <T> runCatchingSafe(block: () -> T): Result<T> =
	try {
		Result.success(block())
	} catch (exception: Exception) {
		Result.failure(exception)
	}
