#!/bin/bash

cloudflare_ddns_binary=$(which cloudflare-ddns)

if [[ "$cloudflare_ddns_binary" != "" ]]; then
	echo "Cloudflare ddns is already installed!"
	echo "Replacing it with the latest version"
	dpkg -r cloudflare-ddns
fi

mkdir -p /tmp/cloudflare-ddns-install/bin

wget \
	https://github.com/jqlang/jq/releases/download/jq-1.7.1/jq-linux-amd64 \
	-O /tmp/cloudflare-ddns-install/bin/jq

chmod +x /tmp/cloudflare-ddns-install/bin/jq

latest_release=$(
	curl -L \
	  -H "Accept: application/vnd.github+json" \
	  -H "X-GitHub-Api-Version: 2022-11-28" \
	  https://api.github.com/repos/magonxesp/cloudflare-ddns/releases/latest
)

package_url=$(
	echo "$latest_release" | \
	/tmp/cloudflare-ddns-install/bin/jq -r '.assets[].browser_download_url | select(endswith("_amd64.deb"))'
)

wget \
	"$package_url" \
	-O /tmp/cloudflare-ddns-install/cloudflare-ddns.deb

dpkg -i /tmp/cloudflare-ddns-install/cloudflare-ddns.deb
rm -rf /tmp/cloudflare-ddns-install
