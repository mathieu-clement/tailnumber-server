#!/bin/sh

mkdir -p $HOME/tailnumber-data/faa/ReleasableAircraft
cd $HOME/tailnumber-data/faa/ReleasableAircraft && rm *
curl -s -S -o ReleasableAircraft.zip "https://registry.faa.gov/database/ReleasableAircraft.zip"
unzip -q ReleasableAircraft.zip
rm ReleasableAircraft.zip DOCINDEX.txt DEREG.txt DEALER.txt RESERVED.txt ardata.pdf