#!/bin/bash

USAGE="USAGE $0 DBNAME"

if [ -z "$1" ]; then
	echo $USAGE
	exit 1
fi

read -s -p "db password: " PGPASSWORD
echo
export PGPASSWORD

DBNAME=$1
QUERY="select usr_id from users where usr_lastlogin is not null and usr_id not in (select usr_id from usr_private_attrs) order by usr_id;"
IDS=$(psql -t -d $DBNAME -U kir -h localhost -c "$QUERY")
TMPFILE=/tmp/dump.txt

rm -f $TMPFILE

for id in $IDS; do
	for attr in CELL_PHONE EMAIL SCREEN_NAME ROOM_NUMBER; do
		echo "$id,$attr,true" >> $TMPFILE
	done
done

if [ -f $TMPFILE ]; then
	COPYQUERY="COPY usr_private_attrs (usr_id, attr_name, visible) FROM STDIN (FORMAT csv)"
	psql -d $DBNAME -U kir -h localhost -c "$COPYQUERY" < $TMPFILE
	rm -f $TMPFILE
fi
unset PGPASSWORD
