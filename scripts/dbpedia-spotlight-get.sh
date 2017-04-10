#!/bin/bash

CURL=`which curl`

MINARGS=3

HELP='Get annotation properties from DBPediaSpotlight. Usage:
	'$0' <text> <confidence> <language>.
	Ex: '$0' "Dell Notebook Core i5 8GB Ram 256GB SSD 2.3GHz" 0.05 en'

if [ $# -ne "$MINARGS" ]; then
    echo -e "$HELP \n"
    exit -1
fi

TEXT=$1
CONFIDENCE=$2
LANGUAGE=$3

URL="http://model.dbpedia-spotlight.org/$LANGUAGE/annotate"

${CURL} ${URL}  \
  --data-urlencode "text=$TEXT" \
  --data "confidence=$CONFIDENCE" \
  --header "Accept: application/json"
