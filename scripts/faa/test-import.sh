#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:importFaa --args="/home/tailnumber/tailnumber-data/faa/ReleasableAircraft"
