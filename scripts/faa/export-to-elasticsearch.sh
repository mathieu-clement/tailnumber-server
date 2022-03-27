#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:exportFaaToElasticSearch --args="US /home/tailnumber/tailnumber-data/faa/ReleasableAircraft"
