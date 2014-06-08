#!/bin/bash
set -e

docker run --name pek-data pek-data-img

docker run -itd -p 5432:5432 --volumes-from pek-data -e POSTGRESQL_PASS=dev --name pek-postgres pek-postgres-img

docker run -itdP --volumes-from pek-data --link pek-postgres:db --name pek-wildfly pek-wildfly-img

