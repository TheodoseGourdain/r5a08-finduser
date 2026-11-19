#!/bin/sh
set -eu

docker exec -i redis redis-cli <<EOF
SADD restricted:users:1 2
SADD restricted:users:2 1
SET restricted:threshold:1:2 100
SET restricted:threshold:2:1 100
EOF
