#!/bin/bash

#1st step: update the version of the root project!
#2nd step: run this script

#clean and build with the updated root project
$MVN_HOME/bin/mvn clean install

#update parent.version of child projects
$MVN_HOME/bin/mvn -DgenerateBackupPoms=false versions:update-parent

#read version from root pom
CURRENT_VERSION=`$MVN_HOME/bin/mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }'`

echo Version: $CURRENT_VERSION

#writing to property file
FILE=sch-pek-web/src/main/java/hu/sch/web/PhoenixApplication.properties
sed -i -e s/app\.version=.*/app.version="$CURRENT_VERSION"/ $FILE

#clean and build with the updated child projects
$MVN_HOME/bin/mvn clean install
