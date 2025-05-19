#!/usr/bin/env bash
# Requires vegeta tool.
# Requires system to be up and running. Run docker compose up before this script.
vegeta attack -targets=targets.http -redirects=-1 -rate=100/s -duration=10m | vegeta report -every=5s