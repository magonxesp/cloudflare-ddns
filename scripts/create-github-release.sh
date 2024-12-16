#!/bin/bash

gradle_build_fail_exit_code=1
docker_not_found_fail_exit_code=2
package_build_fail_exit_code=3

version=$(git describe --tags --abbrev=0)
project_version=$(echo "$version" | sed 's/^v//g')

echo "Building packages for version $version"
echo "Project version is $project_version"

mkdir -p build/packages

./gradlew -Pgit.tag="$project_version" clean build

if [[ $? -gt 0 ]]; then
	echo "Failed building the application jar"
	exit $gradle_build_fail_exit_code
fi

docker=$(which docker)
echo "Docker binary $docker"

if [[ "$docker" == "" ]]; then
	echo "docker command not found"
	exit $docker_not_found_fail_exit_code
fi

./scripts/docker-make-deb-package.sh

if [[ $? -gt 0 ]]; then
  	echo "Failed to build debian package"
  	exit $package_build_fail_exit_code
fi

# This will be experimental
# For now we only build for debian based linux distros
#if [[ "$(uname)" == "Darwin" ]]; then
#  	./scripts/make-package.sh macos
#
#	if [[ $? -gt 0 ]]; then
#    	echo "Failed to build macOS package"
#    	exit $package_build_fail_exit_code
#	fi
#else
#	echo "⚠️ Skipping macos build because the actual system is not macOS"
#	echo "⚠️ You need a macOS machine for allow target to macOS"
#fi

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
