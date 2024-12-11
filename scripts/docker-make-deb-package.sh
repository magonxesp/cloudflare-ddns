#!/bin/bash

mkdir -p build/packages

docker build -f package/deb/Dockerfile -t deb-packager:latest .

docker run \
  --rm \
  -v ./build/packages:/builder/build/packages \
  deb-packager

sudo chown -R "$UID:$GID" build/packages
