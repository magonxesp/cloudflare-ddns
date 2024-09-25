# Cloudflare DDNS
Program for update the hostnames ip on Cloudflare DNS records

Requeriments:
* Gradle 8.7
* Java 21
* Curl
* libcurl4-gnutls-dev

## Build
Build the native image executable

```sh
$ ./gradlew build
```
And get the executables files in ```build/bin``` directory

## Install on linux

### For your current user
```sh
mkdir -p $HOME/.local/bin
cp build/bin/linuxX64/releaseExecutable/cloudflare-ddns.kexe $HOME/.local/bin/cloudflare-ddns
chmod +x $HOME/.local/bin/cloudflare-ddns
```

Later you need to export the ``$HOME/.local/bin`` directory to the ``PATH`` variable if you dont have it exported

```sh
echo 'export PATH=$HOME/.local/bin:$PATH' >> $HOME/.bashrc
source $HOME/.bashrc
```

### For all users
```sh
sudo cp build/bin/linuxX64/releaseExecutable/cloudflare-ddns.kexe /usr/bin/cloudflare-ddns
sudo chmod +x /usr/bin/cloudflare-ddns
```

## Usage

* Configure the program
```shell script
$ cloudflare-ddns configure
```
* Updating the configured hostnames ip with the current ip
```shell script
$ cloudflare-ddns sync
```
