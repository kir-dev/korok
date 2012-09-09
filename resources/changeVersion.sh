#!/bin/bash

#update parent.version of child projects
$MVN_HOME/bin/mvn -DgenerateBackupPoms=false versions:update-parent

#read version from root pom
CURRENT_VERSION=`$MVN_HOME/bin/mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }'`

echo Version: $CURRENT_VERSION

#writing to property file
FILE=sch-pek-web/src/main/java/hu/sch/web/PhoenixApplication.properties
sed -i -e s/app\.version=.*/app.version="$CURRENT_VERSION"/ $FILE
