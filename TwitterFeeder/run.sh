#!/bin/bash

# This is a helper script to run the application
# After modifying it to your project, you can use it as following:
#
# ./run.sh LinkListener
# ./run.sh SearchResultsServer
# ./run.sh TwitterListener

# Configure AWS environment variable for SDK API

# Run the java program with different main as user provided
# Also send the correct system properties (don't forget backslash before newline)
java -cp TwitterFeeder-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -DRDS_DB_NAME="blabla" \
    -DRDS_USERNAME="blabla" \
    -DRDS_PASSWORD="blabla" \
    -DRDS_HOSTNAME="blabla" \
    -DRDS_PORT="blabla" \
    -DBUCKET="blabla" \
    -DKEY="blabla" \
    -Dconfig.twitter.consumer.key=blabla \
    -Dconfig.twitter.consumer.secret=blabla \
    -Dconfig.twitter.access.token=blabla \
    -Dconfig.twitter.access.secret=blabla \
    -Dconfig.twitter.track="filter" \
    -Dconfig.sqs.url="blabla \
    -Dconfig.aws.id=blabla \
    -Dconfig.aws.secret.key=blabla \
    il.ac.colman.cs.$1
