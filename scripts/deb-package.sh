#!/bin/bash

mkdir -p build/tmp/jpackage

jpackage \
		--name cloudflare-ddns \
		--about-url 'https://github.com/magonxesp/cloudflare-ddns' \
		--app-version $(cat build/libs/version.txt) \
		--input build/libs \
		--main-jar $(find build/libs -name "*-all.jar" | sed 's/build\/libs\///g') \
		--dest release \
		--type deb \
		--resource-dir package/deb \
		--vendor "MagonxESP" \
		--copyright "MagonxESP, (C) $(date +%Y)" \
    --description "Update the DNS records with your current ip on Cloudflare" \
    --temp build/tmp/jpackage \
		--linux-deb-maintainer 'magonxesp@gmail.com'
