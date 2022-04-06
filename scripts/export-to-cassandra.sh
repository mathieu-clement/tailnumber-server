#!/bin/sh
cd "$(dirname "$0")"/.. || exit
./gradlew :importer:exportToCassandra --args="CH,US $HOME/tailnumber-data"
