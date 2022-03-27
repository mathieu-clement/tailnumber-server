#!/bin/sh
cd "$(dirname "$0")"/../.. || exit
./gradlew :importer:importFaa --args="/Users/mathieuclement/Work/TailNumber/Registries/FAA/ReleasableAircraft"
