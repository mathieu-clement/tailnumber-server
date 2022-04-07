#!/bin/sh

base=$HOME/tailnumber-data/ch

du $base/json/*.json | grep "^0"
