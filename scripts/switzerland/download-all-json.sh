#!/bin/sh

base=$HOME/tailnumber-data/ch

for reg in $($base/extract-tailnumbers.sh | xargs); do
    echo $reg
    $base/fetch-json-for-tailnumber.sh $reg
done
