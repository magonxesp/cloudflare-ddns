# Cloudflare DDNS
A Dynamic DNS client that automatically updates your IP in Cloudflare DNS records, 
ensuring your domain stays accessible without the need for a static IP. 🔥

Requirements:
* Go >= 1.24

## Usage

First you need the ``config.json`` file for configure the Account, API Token and Zone DNS records to be updated.
You can make it downloading the sample configuration.

```sh
curl -L https://raw.githubusercontent.com/magonxesp/cloudflare-ddns/refs/heads/main/example-config.json -o config.json
```

Then when you finish editing the ``config.json`` file you are able to run the program on the same working directory
as ``config.json`` is located.

```sh
cloudflare-ddns
```

Also, you can specify the ``config.json`` path.

```sh
cloudflare-ddns --config /path/to/config.json
```

For more options you can see them with the ``-h`` option.

```sh
cloudflare-ddns -h
```

## Docker image

Cloudflare DDNS is available as docker image too. You can pull ``magonx/cloudflare-ddns`` and use it.

```sh
docker pull magonx/cloudflare-ddns:latest
```

And then run the image.

```sh
docker run magonx/cloudflare-ddns:latest -v ./config.json:/config.json
```

## Build

For build the binary executable you have to run the following command.

```sh
$ go build
```

And then you should see the executable on the working directory.
