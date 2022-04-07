#!/bin/sh

base=$HOME/tailnumber-data/ch
cd $base && tar -a -v -cf json.tar.gz --include='json/HB*.json' json/*