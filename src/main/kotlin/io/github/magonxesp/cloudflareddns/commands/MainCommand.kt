package io.github.magonxesp.cloudflareddns.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class MainCommand : CliktCommand(
    help = "Update IP address to dns records of Cloudflare"
) {
    init {
        subcommands(ConfigureCommand(), SyncCommand())
    }

    override fun run() { }
}
