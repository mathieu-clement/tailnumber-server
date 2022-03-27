#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:exportFaaToCassandra --args="US /Users/mathieuclement/Work/TailNumber/Registries/FAA/ReleasableAircraft"
