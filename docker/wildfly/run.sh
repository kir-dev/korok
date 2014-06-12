#!/bin/bash
set -e

# Values from the linked postgres container
VIR_DB_PASS=$DB_ENV_POSTGRESQL_PASS
VIR_DB_HOST=$DB_PORT_5432_TCP_ADDR
VIR_DB_PORT=$DB_PORT_5432_TCP_PORT

# maybe a bug but if this folder stays then wildfly can't boot next time
# similar to http://stackoverflow.com/questions/20965737/docker-jboss7-war-commit-server-boot-failed-in-an-unrecoverable-manner
BACKUP_FOLDER_NAME=`date "+%Y%m%d-%H%M%S"`
mv $WILDFLY_DIR/standalone/configuration/standalone_xml_history/current $WILDFLY_DIR/standalone/configuration/standalone_xml_history/$BACKUP_FOLDER_NAME

# Start appserver
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &"

sleep 15

# Add db datasource if necessary
CFG_FILE=/tmp/datasource-setup.cli
CFG_COMMANDS=$(cat <<BATCH
if (outcome != success) of /subsystem=datasources/data-source=schpek:read-resource
xa-data-source add --name=schkp --driver-name=postgresql --jndi-name=java:/jdbc/sch --user-name=kir --password=$VIR_DB_PASS --use-ccm=false --max-pool-size=25 --min-pool-size=10 --pool-prefill=true --prepared-statements-cache-size=30 --xa-datasource-properties=[{ServerName=$VIR_DB_HOST}, {DatabaseName=vir}, {PortNumber=$VIR_DB_PORT}]
end-if
BATCH
)

echo "$CFG_COMMANDS" > $CFG_FILE
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/jboss-cli.sh -c --file=$CFG_FILE"
rm $CFG_FILE

/usr/sbin/sshd -D

