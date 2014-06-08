#!/bin/bash
set -e

POSTGRESQL_DATA="/var/lib/postgresql/$PG_VERSION/main/"
POSTGRESQL_PASS=${POSTGRESQL_PASS:-"docker"}

# rm -rf $POSTGRESQL_DATA
if [ ! -d $POSTGRESQL_DATA ]; then
  mkdir -p $POSTGRESQL_DATA
  chown -R postgres:postgres $POSTGRESQL_DATA
  su postgres sh -c "/usr/lib/postgresql/$PG_VERSION/bin/initdb -D $POSTGRESQL_DATA"

  service postgresql start
  su postgres sh -c "psql -c \"CREATE ROLE kir ENCRYPTED PASSWORD '$POSTGRESQL_PASS' NOSUPERUSER CREATEDB NOCREATEROLE INHERIT LOGIN;\"" && \
  su postgres sh -c "createdb -E utf8 -O kir vir"
  su postgres sh -c "createdb -E utf8 -O kir vir-test"

  # load db
  su postgres sh -c "psql -d vir < /data/appdata/vir-dump.sql"
fi

# make sure the service is not running because we have to start it manually
# so the container won't exit immediately
service postgresql stop

su postgres -c "/usr/lib/postgresql/$PG_VERSION/bin/postgres -D $POSTGRESQL_DATA -c config_file=/etc/postgresql/$PG_VERSION/main/postgresql.conf"

