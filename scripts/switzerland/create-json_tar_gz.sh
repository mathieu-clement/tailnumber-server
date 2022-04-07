#!/bin/sh

base=$HOME/tailnumber-data/ch
cd $base && tar -a -v -cf json.tar.gz  json/*.json
