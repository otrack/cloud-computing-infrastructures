#!/usr/bin/env bash

if [ -z "$1" ]; then
    TAG=latest
else
    TAG="$1"
fi

DOCKER_USER=$(docker info |
    grep Username |
    awk '{print $2}')
if [ -z "${DOCKER_USER}" ]; then
    DOCKER_USER=0track
fi

DIR=$(dirname "$0")
IMAGE=${DOCKER_USER}/transactions:${TAG}
DOCKERFILE=${DIR}/../docker/Dockerfile

# package
mvn clean package -DskipTests

# last commit hash
git log -1 --format="%H" >${DIR}/../../../transactions-version

# build image
docker build \
    --no-cache \
    -t "${IMAGE}" -f "${DOCKERFILE}" .

# push image
# xdocker push "${IMAGE}"
