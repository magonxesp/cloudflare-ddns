package io.github.magonxesp.cloudflareddns

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt

class ConfigureCommand : CliktCommand(
    name = "configure",
    help = "Create a new configuration"
) {
    val zoneId by option().prompt("Cloudflare Zone ID")
    val apiKey by option().prompt("Cloudflare API Key")
    val hostNames by option().prompt("Host names will be updated (separated by comma)")

    override fun run() {
        val configuration = Configuration(
            zoneId = zoneId,
            apiKey = apiKey,
            hostNames = hostNames.trim().split(",").map { it.trim() }
        )

        configuration.save()
        echo("\u2728 Done! godaddyddns command is now configured! \u2728")
        echo("Saved in ${Configuration.configFile.absolutePath}")
    }
}

class SyncCommand : CliktCommand(
    name = "sync",
    help = "Update the current public IP address to the configured Cloudflare's hostnames"
) {
    override fun run() {
        val configuration = Configuration.read()
        val ipifyClient = IpifyClient()
        val cloudflareClient = CloudflareClient(configuration.zoneId, configuration.apiKey)

        echo("Updating hostnames ip address")

        val currentIpAddress = ipifyClient.fetchCurrentPublicIpAddress()
        echo("Current ip address: $currentIpAddress")
        echo("⚠\uFE0F Hostnames will be updated to ip address $currentIpAddress ⚠\uFE0F")

        val records = cloudflareClient.getDNSRecords().filter {
            configuration.hostNames.contains(it.name)
        }

        for (record in records) {
            cloudflareClient.updateHostName(record, currentIpAddress)
            echo("\uD83D\uDFE2 Hostname ${record.name} updated!")
        }

        echo("\u2728 Done! All hostnames are updated to Cloudflare! \u2728")
    }
}

class MainCommand : CliktCommand(
    help = "Update IP address to dns records of Cloudflare"
) {
    init {
        subcommands(ConfigureCommand(), SyncCommand())
    }

    override fun run() { }
}

fun main(args: Array<String>) = MainCommand().main(args)
