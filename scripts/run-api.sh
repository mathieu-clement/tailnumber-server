#!/bin/sh
# Please read https://stackoverflow.com/q/44701947 to increment -Xms and -Xmx
cd "$(dirname "$0")"/.. || exit
./gradlew :api:run
