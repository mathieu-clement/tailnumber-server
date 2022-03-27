#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:exportFaaToCassandra --args="US /home/tailnumber/tailnumber-data/faa/ReleasableAircraft"
