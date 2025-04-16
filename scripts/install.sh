#!/bin/bash

$base_url="https://github.com/magonxesp/cloudflare-ddns/releases/latest/download"
$arch=$(uname -m)
$platform=$(uname -s)

if [[ "$platform" == "Darwin" ]] && [[ "$arch" == "arm64" ]]
then
	$artifact="cloudflare-ddns-darwin-arm64"
elif [[ "$platform" == "Darwin" ]] && [[ "$arch" == "x86_64" ]]
then
	$artifact="cloudflare-ddns-darwin-amd64"
elif [[ "$platform" == "Linux" ]] && [[ "$arch" == "arm64" ]]
then
	$artifact="cloudflare-ddns-linux-arm64"
elif [[ "$platform" == "Linux" ]] && [[ "$arch" == "x86_64" ]]
then
	$artifact="cloudflare-ddns-linux-amd64"
else
	echo "Unsupported platform or architecture"
	exit 1
fi

which curl

if [[ $? -neq 0 ]]; then
	echo "You need curl installed"
	exit 2
fi

curl -o /tmp/cloudflare-ddns -L "$base_url/$artifact"
mv /tmp/cloudflare-ddns /usr/local/bin/cloudflare-ddns
chmod -x /usr/local/bin/cloudflare-ddns
