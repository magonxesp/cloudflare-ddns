#!/bin/bash

wget \
	"https://github.com/magonxesp/cloudflare-ddns/releases/download/v0.4.0/cloudflare-ddns_0.4.0_amd64.deb" \
	-O /tmp/cloudflare-ddns.deb > /dev/null

dpkg -i /tmp/cloudflare-ddns.deb
rm -rf /tmp/cloudflare-ddns.deb
