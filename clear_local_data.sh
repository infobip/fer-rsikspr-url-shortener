#!/usr/bin/env bash
# Requires the find tool.
set -e
find ./otel-lgtm/data -not -name '.keep' -delete
find ./urls-db/data -not -name '.keep' -delete
find ./stats-db/data -not -name '.keep' -delete
find ./events-queue/data -not -name '.keep' -delete
find ./urls-cache -not -name '.keep' -delete
rm -f ./vegeta/targets.http