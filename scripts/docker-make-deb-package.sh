#!/bin/bash

mkdir -p build/packages

docker build -f package/deb/Dockerfile -t deb-packager:latest .

docker run \
	--rm \
	-v ./build/packages:/builder/build/packages \
	deb-packager:latest

if [[ "$GID" == "" ]]; then
	owner="$UID:$UID"
else
	owner="$UID:$GID"
fi

sudo chown -R "$owner" build/packages
