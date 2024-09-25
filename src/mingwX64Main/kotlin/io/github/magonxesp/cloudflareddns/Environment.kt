package io.github.magonxesp.cloudflareddns

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual val homeDir: String = getenv("homedrive")?.toKString() + getenv("homepath")?.toKString()
