#!/bin/sh -x
base=$HOME/tailnumber-data/ch
grep "HB-" $base/pubs.html | grep -x ".\{17,20\}" | grep -oEi 'HB-[0-9A-Z-]+' | sort
