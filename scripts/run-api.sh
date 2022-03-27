#!/bin/sh
cd "$(dirname "$0")"/.. || exit
./gradlew :api:run
