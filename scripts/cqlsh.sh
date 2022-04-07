#!/bin/sh
docker run --name cqlsh --rm -it --network host nuvo/docker-cqlsh cqlsh localhost 9042 --cqlversion='3.4.5'
