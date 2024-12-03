#!/bin/bash

mkdir -p build/tmp/jpackage

common_options=(
  --name cloudflare-ddns
  --about-url 'https://github.com/magonxesp/cloudflare-ddns'
  --app-version "$(cat build/libs/version.txt)"
  --input build/libs
  --main-jar "$(find build/libs -name "*-all.jar" | sed 's/build\/libs\///g')"
  --dest release
  --vendor 'MagonxESP'
  --copyright "MagonxESP, (C) $(date +%Y)"
  --description 'Update the DNS records with your current ip on Cloudflare'
  --temp build/tmp/jpackage
  --license-file LICENSE
)

platform=$1
platform_opts=()

if [[ "$platform" == "debian" ]]; then
  platform_opts=(
    --type deb
  	--resource-dir package/deb
  	--linux-deb-maintainer 'magonxesp@gmail.com'
  )
fi

if [[ "$platform" == "macos" ]]; then
  platform_opts=(
    --type pkg
  )
fi

jpackage "${common_options[@]}" "${platform_opts[@]}"
