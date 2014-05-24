#!/bin/bash

APPDATA=$1
OUTFILE=app-setup.cli
TMPDIR=/tmp
OUTPATH=$TMPDIR/$OUTFILE

if [ -z "$APPDATA" ]; then
	echo "Usage: ./app-setup.sh APPDATA"
	echo "APPDATA: path to appdata directory"
	exit
fi

read -s -p "db password: " DB_PASS
# print new line
echo

OUTPUT=$(cat <<BATCH
xa-data-source add --name=schkp --driver-name=postgresql --jndi-name=java:/jdbc/sch --user-name=kir --password=$DB_PASS --use-ccm=false --max-pool-size=25 --min-pool-size=10 --pool-prefill=true --prepared-statements-cache-size=30 --xa-datasource-properties=[{ServerName=localhost}, {DatabaseName=vir}, {PortNumber=5432}]
/subsystem=mail/mail-session="java:/mail/korokMail":add(from=kir-dev@sch.bme.hu,jndi-name=java:/mail/korokMail)
/system-property=application.resource.dir:add(value=$APPDATA)
BATCH
)

if [ -z "$JBOSS_HOME" ]; then
	echo "No JBOSS_HOME specified, dumping config to $OUTFILE"
	echo "$OUTPUT" > $OUTFILE
else
	echo "$OUTPUT" > $OUTPATH
	$JBOSS_HOME/bin/jboss-cli.sh -c --file=$OUTPATH
	rm $OUTPATH
fi

