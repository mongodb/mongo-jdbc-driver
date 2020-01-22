#!/bin/bash
# Copyright (c) 2020-Present MongoDB Inc.
echo $0

if [ "$PLATFORM" = "" ]; then
    PLATFORM=linux
    echo "WARNING: no value provided for \$PLATFORM: using default of '$PLATFORM'"
fi

case "$PLATFORM" in
ubuntu1804-64-jdk-8)
    PLATFORM_NAME='linux'
    JAVA_HOME=/opt/java/jdk8
    ;;
ubuntu1804-64-jdk-11)
    PLATFORM_NAME='linux'
    JAVA_HOME=/opt/java/jdk11
    ;;
*)
    echo "ERROR: invalid value for \$PLATFORM: '$PLATFORM'"
    echo "Allowed values: 'ubuntu1604-64-jdk-8', 'ubuntu1604-64-jdk-11'"
    exit 1
    ;;
esac
