#!/bin/bash
# Copyright (c) 2020-Present MongoDB Inc.
# This file should be sourced by all scripts

# we start by sourcing platforms.sh. this will set environment variables that
# differ depending on which platform we are building on
# shellcheck source=platforms.sh
. "$(dirname "$0")/platforms.sh"

set -o verbose

# create variables for a number of useful directories
SCRIPT_DIR=$(cd $(dirname $0) && pwd -P)

PROJECT_ROOT="$SCRIPT_DIR/../"

basename=${0##*/}
ARTIFACTS_DIR="$PROJECT_ROOT/artifacts"
BUILD_DIR="$PROJECT_ROOT/build"
# get the version from the gradle.properties file, so we don't need to update in two places.
MDBJDBC_VER="$(cat "$PROJECT_ROOT/gradle.properties" | head -n 1 | sed  s'/version = //')"

# export any environment variables that will be needed by subprocesses
export MDBJDBC_VER
export JAVA_HOME

# Each script should run with errexit set and should start in the project root.
# In general, scripts should reference directories via the provided environment
# variables instead of making assumptions about the working directory.
cd "$PROJECT_ROOT"
