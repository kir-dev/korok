#!/bin/bash

DB_NAME=${1:-vir}
OUTFILE=/tmp/droptables.sql

psql -t -U kir -h localhost -d $DB_NAME -c "SELECT 'DROP TABLE ' || n.nspname || '.' || c.relname || ' CASCADE;' FROM pg_catalog.pg_class AS c LEFT JOIN pg_catalog.pg_namespace AS n ON n.oid = c.relnamespace WHERE relkind = 'r' AND n.nspname NOT IN ('pg_catalog', 'pg_toast') AND pg_catalog.pg_table_is_visible(c.oid)" > $OUTFILE

psql -U kir -h localhost -d $DB_NAME -f $OUTFILE
rm $OUTFILE
