#!/bin/bash

mkdir -p release

docker build -f package/deb/Dockerfile -t deb-packager:latest .

docker run \
  -v ./release:/builder/release \
  deb-packager

sudo chown -R "$UID:$GID" release
