#!/usr/bin/env bash
# Requires the mvn tool.
set -e
mvn -f ./api-gateway/pom.xml clean test
mvn -f ./recording-service/pom.xml clean test
mvn -f ./redirect-service/pom.xml clean test
mvn -f ./shortener-service/pom.xml clean test
