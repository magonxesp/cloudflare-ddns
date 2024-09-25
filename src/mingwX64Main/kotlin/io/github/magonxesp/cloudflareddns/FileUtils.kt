package io.github.magonxesp.cloudflareddns

actual fun path(vararg paths: String): String = paths.joinToString("\\")
