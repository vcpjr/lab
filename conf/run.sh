#!/bin/bash

java -Dlogging.config="conf/logback.xml" \
     -jar ./Lab.jar "$@"
