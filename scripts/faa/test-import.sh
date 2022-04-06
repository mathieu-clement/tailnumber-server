#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:importFaa --args="$HOME/tailnumber-data/faa/ReleasableAircraft"
