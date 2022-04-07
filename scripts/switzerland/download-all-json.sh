#!/bin/sh

scriptdir=$(dirname $0)
base=$HOME/tailnumber-data/ch2
parallel_connections=4

$scriptdir/extract-tailnumbers.sh | xargs -L 1 -n 1 -P $parallel_connections $scriptdir/fetch-json-for-tailnumber.sh
