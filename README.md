# Cloudflare DDNS
Program for update the hostnames ip on Cloudflare DNS records

Requeriments:
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
$ java -jar cloudflare-ddns.jar configure
```
* Updating the configured hostnames ip with the current ip
```shell script
$ java -jar cloudflare-ddns.jar cloudflare-ddns sync
```
