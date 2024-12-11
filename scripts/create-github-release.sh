#!/bin/bash

version=$(git describe --tags --abbrev=0)
project_version=$(echo "$version" | sed 's/^v//g')
echo "Building packages for version $version"
echo "Project version is $project_version"

mkdir -p build/packages

./gradlew -PgitTag="$project_version" clean build \
&& ./scripts/docker-make-deb-package.sh \
&& ./scripts/make-package.sh macos

if [[ $? -gt 0 ]]; then
  echo "Failed to build the packages"
  exit 1
fi

mkdir -p build/tmp
git cliff --latest > build/tmp/changes.md

mkdir -p build/tmp/github-release
cp build/libs/cloudflare-ddns-*-all.jar build/tmp/github-release/
cp build/packages/* build/tmp/github-release/

gh release create \
 --repo magonxesp/cloudflare-ddns \
 -F build/tmp/changes.md \
 --latest=false \
 "$version"  \
 'build/tmp/github-release/*'
