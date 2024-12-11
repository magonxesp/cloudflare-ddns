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

## Usage

* Configure the program
```shell script
$ cloudflare-ddns configure
```
* Updating the configured hostnames ip with the current ip
```shell script
$ cloudflare-ddns sync
```
