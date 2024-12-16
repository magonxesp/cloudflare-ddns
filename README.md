# Cloudflare DDNS
Update the DNS records with your current ip on Cloudflare

Requirements:
* Gradle 8.7
* Java 21

## Build

```sh
$ ./gradlew build
```
And get the jar files in ```build/libs``` directory

## Installation

For Debian AMD64

```sh
curl -Ls https://raw.githubusercontent.com/magonxesp/cloudflare-ddns/refs/heads/main/scripts/install-debian-amd64.sh | sudo bash
```

## Usage
* Help
```sh
$ cloudflare-ddns -h
```

* Configure the program with your Cloudflare DNS hostnames
```sh
$ cloudflare-ddns configure
```

* Updating the configured hostnames ip with the current ip
```sh
$ cloudflare-ddns sync
```
