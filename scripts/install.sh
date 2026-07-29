#!/usr/bin/env bash

set -euo pipefail

repository="magonxesp/cloudflare-ddns"
base_url="https://github.com/$repository/releases/latest/download"

if [[ "$(uname -s)" != "Linux" ]]; then
  echo "cloudflare-ddns packages are only available for Linux" >&2
  exit 1
fi

if [[ "${EUID:-$(id -u)}" -ne 0 ]]; then
  echo "Run this installer as root, for example with sudo" >&2
  exit 1
fi

for command in curl dpkg mktemp; do
  if ! command -v "$command" >/dev/null 2>&1; then
    echo "Required command not found: $command" >&2
    exit 2
  fi
done

architecture="$(dpkg --print-architecture)"
case "$architecture" in
  amd64 | arm64)
    ;;
  *)
    echo "Unsupported Debian architecture: $architecture" >&2
    exit 1
    ;;
esac

artifact="cloudflare-ddns-linux-$architecture.deb"
temporary_directory="$(mktemp -d)"
package="$temporary_directory/$artifact"

cleanup() {
  rm -rf "$temporary_directory"
}
trap cleanup EXIT

echo "Downloading $artifact from the latest GitHub release"
curl \
  --fail \
  --location \
  --show-error \
  --output "$package" \
  "$base_url/$artifact"

dpkg --install "$package"
