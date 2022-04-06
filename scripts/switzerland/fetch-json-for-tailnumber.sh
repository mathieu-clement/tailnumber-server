#!/bin/sh -e
REGISTRATION=$1
base=$HOME/tailnumber-data/ch
mkdir -p $base/json
curl 'https://app02.bazl.admin.ch/web/bazl-backend/lfr' -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -H 'Referer: https://app02.bazl.admin.ch/web/bazl/en/' --data-raw '{"page_result_limit":100,"current_page_number":1,"sort_list":"registration","language":"en","queryProperties":{"registration":"'$REGISTRATION'","aircraftStatus":["Registered","Reserved","Reservation Expired","Registration in Progress"]}}' > $base/json/$REGISTRATION.json
