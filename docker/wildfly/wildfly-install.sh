#!/bin/bash
#title           :wildfly-install.sh
#description     :The script to install Wildfly 8.x
#more            :http://sukharevd.net/wildfly-8-installation.html
#author	         :Dmitriy Sukharev & Balazs Varga
#date            :20140608

WILDFLY_FILENAME="wildfly-$WILDFLY_VERSION"
WILDFLY_ARCHIVE_NAME="$WILDFLY_FILENAME.tar.gz"
WILDFLY_DOWNLOAD_ADDRESS="http://download.jboss.org/wildfly/$WILDFLY_VERSION/$WILDFLY_ARCHIVE_NAME"

PG_JDBC_ARCHIVE_NAME="postgresql-$PG_JDBC_VERSION.jdbc41.jar"
PG_JDBC_DOWNLOAD_ADDRESS="http://jdbc.postgresql.org/download/$PG_JDBC_ARCHIVE_NAME"

WILDFLY_FULL_DIR="$INSTALL_DIR/$WILDFLY_FILENAME"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root."
   exit 1
fi

mkdir -p $INSTALL_DIR

echo "Downloading: $WILDFLY_DOWNLOAD_ADDRESS..."
curl -s $WILDFLY_DOWNLOAD_ADDRESS | tar xz -C $INSTALL_DIR
ln -s $WILDFLY_FULL_DIR/ $WILDFLY_DIR

echo "Configuration..."

# postgres jdbc driver
PG_JDBC_TARGET_DIR="$WILDFLY_DIR/modules/system/layers/base/org/postgresql/main"
curl -s --create-dirs $PG_JDBC_DOWNLOAD_ADDRESS -o $PG_JDBC_TARGET_DIR/$PG_JDBC_ARCHIVE_NAME
if [ $? -ne 0 ]; then
  echo "Not possible to download jdbc driver."
  exit 1
fi

MODULE_XML=$(cat <<BATCH
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.0" name="org.postgresql">
    <resources>
        <resource-root path="$PG_JDBC_ARCHIVE_NAME"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
BATCH
)

echo "$MODULE_XML" > $PG_JDBC_TARGET_DIR/module.xml

useradd -s /bin/bash $WILDFLY_USER
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/
chmod -R +w $WILDFLY_DIR/

# We need to add a management user to use the console
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/add-user.sh admin Admin#70365 --silent"

# This will boot WildFly in the standalone mode and bind to all interface
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &"

sleep 15

CFG_FILE=/tmp/app-setup.cli
CFG_COMMANDS=$(cat <<BATCH
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
/subsystem=mail/mail-session="java:/mail/korokMail":add(from=kir-dev@sch.bme.hu,jndi-name=java:/mail/korokMail)
/system-property=application.resource.dir:add(value=$PEK_APPDATA)
/profile=default/subsystem=web/virtual-server=default-host:write-attribute(name=enable-welcome-root,value=false)
BATCH
)

echo "$CFG_COMMANDS" > $CFG_FILE
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/jboss-cli.sh -c --file=$CFG_FILE"
su $WILDFLY_USER -c "$WILDFLY_DIR/bin/jboss-cli.sh -c --command=shutdown"
rm $CFG_FILE

# maybe a bug but if this folder stays then wildfly can't boot next time
BACKUP_FOLDER_NAME=date "+%Y%m%d-%s"
mv $WILDFLY_DIR/standalone/configuration/standalone_xml_history/current $WILDFLY_DIR/standalone/configuration/standalone_xml_history/$BACKUP_FOLDER_NAME

echo "Wildfly installed and configured."

