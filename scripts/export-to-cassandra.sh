#!/bin/sh
cd "$(dirname "$0")"/.. || exit
./gradlew :importer:exportToCassandra --args="CH,US /home/tailnumber/tailnumber-data"
