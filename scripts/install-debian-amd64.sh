#!/bin/bash

wget \
	"https://github.com/magonxesp/cloudflare-ddns/releases/download/v0.4.1/cloudflare-ddns_0.4.1_amd64.deb" \
	-O /tmp/cloudflare-ddns.deb

dpkg -i /tmp/cloudflare-ddns.deb
rm -rf /tmp/cloudflare-ddns.deb
