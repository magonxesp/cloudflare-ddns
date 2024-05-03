# Cloudflare DDNS
Program for update the hostnames ip on Cloudflare DNS records

Requeriments:
* Gradle 8.7
* GraalVM 21

## Build
Build the native image executable

```sh
$ ./gradlew nativeCompile
```
And get the executable file in ```build/native/nativeCompile``` directory

Or build a ``jar`` archive

```sh
$ ./gradlew build
```

The executable jar will be ```build/libs/cloudflare-ddns-0.0.0-all.jar```

## Usage
### Using the executable file
* Configure the program
```shell script
$ godaddyddns configure
```
* Updating the configured hostnames ip with the current ip
```shell script
$ godaddyddns sync
```
