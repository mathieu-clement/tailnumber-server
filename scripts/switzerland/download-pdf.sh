#!/bin/sh -x

current_date=`date "+%Y-%m-%d"` # date -I doesn't work on Mac

curl 'https://app02.bazl.admin.ch/web/bazl-backend/lfr/publication-pdf' -X POST -H 'Accept-Language: en-US,en;q=0.5' -H 'Accept-Encoding: gzip, deflate, br' -H 'Content-Type: application/json' -H 'Origin: https://app02.bazl.admin.ch' -H 'Referer: https://app02.bazl.admin.ch/web/bazl/en/' -H 'Sec-Fetch-Dest: empty' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-origin' --data-raw '{"page_result_limit":10,"current_page_number":1,"sort_list":"registration","language":"en","queryProperties":{"aircraftStatus":["Registered","Registration in Progress"],"issueDate":"'$current_date'"}}' -o pub.pdf
