#!/bin/sh
cd "$(dirname "$0")"/.. || exit
./gradlew :importer:exportToElasticSearch --args="CH,US $HOME/tailnumber-data"
