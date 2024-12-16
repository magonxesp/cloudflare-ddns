#!/bin/bash

wget \
	https://github.com/magonxesp/cloudflare-ddns/releases/download/v0.3.7/cloudflare-ddns_0.3.7_amd64.deb \
	-O /tmp/cloudflare-ddns.deb

dpkg -i /tmp/cloudflare-ddns.deb
rm /tmp/cloudflare-ddns.deb
