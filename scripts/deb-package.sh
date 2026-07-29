#!/usr/bin/env bash

set -euo pipefail

build_type="${1:-release}"
architecture="${2:-$(dpkg --print-architecture)}"
version="$(cat VERSION)"
target_directory="target/$build_type"
bundle_directory="$target_directory/bundle"
package_directory="$bundle_directory/deb"
binary="$target_directory/cloudflare-ddns"
package="$bundle_directory/cloudflare-ddns_${version}_${architecture}.deb"

if [[ "$build_type" != "release" && "$build_type" != "debug" ]]; then
  echo "Build type must be release or debug" >&2
  exit 1
fi

if [[ ! -x "$binary" ]]; then
  echo "Executable not found at $binary; build it before creating the package" >&2
  exit 1
fi

rm -rf "$package_directory"
mkdir -p "$package_directory/DEBIAN"
mkdir -p "$package_directory/usr/bin"
mkdir -p "$package_directory/usr/share/cloudflare-ddns"

cat > "$package_directory/DEBIAN/control" <<EOF
Package: cloudflare-ddns
Architecture: $architecture
Version: $version
Section: net
Priority: optional
Maintainer: MagonxESP <janma.360@gmail.com>
Description: Cloudflare dynamic DNS client
 Updates Cloudflare A and AAAA records with the current public IP address.
Homepage: https://github.com/magonxesp/cloudflare-ddns
EOF

install -m 0755 "$binary" "$package_directory/usr/bin/cloudflare-ddns"
install -m 0644 \
  example-config.yaml \
  "$package_directory/usr/share/cloudflare-ddns/config.yaml"

dpkg-deb \
  --root-owner-group \
  --build \
  "$package_directory" \
  "$package"

echo "Debian package: $package"
