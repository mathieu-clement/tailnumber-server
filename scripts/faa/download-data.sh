#!/bin/sh

mkdir -p $HOME/tailnumber-data/faa
cd $HOME/tailnumber-data/faa
rm -rf ReleasableAircraft*
curl -s -S -o ReleasableAircraft.zip "https://registry.faa.gov/database/ReleasableAircraft.zip"
unzip -q ReleasableAircraft.zip