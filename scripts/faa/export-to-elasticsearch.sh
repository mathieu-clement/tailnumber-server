#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:exportFaaToElasticSearch --args="US /Users/mathieuclement/Work/TailNumber/Registries/FAA/ReleasableAircraft"
