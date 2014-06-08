#!/bin/bash
set -e

cd data/
docker build -t pek-data-img .

cd ../postgres
docker build -t pek-postgres-img .

cd ../wildfly
docker build -t pek-wildfly-img .

