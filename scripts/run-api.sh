#!/bin/sh
# Please read https://stackoverflow.com/q/44701947 to increment -Xms and -Xmx
cd "$(dirname "$0")"/.. || exit
export JAVA_OPTS="-Xms256m -Xmx2g"
./gradlew :api:run
