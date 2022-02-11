#!/bin/bash
# 
# Usage: run_adl.sh <operation>
# operation: 'start' or 'stop'
#
# This script will start a local mongod and Atlas Data Lake instance, used for integration testing.
# The supported platforms are macos, ubuntu1804, and rhel7.
#
# - To skip the download of ADL, set the environment variable HAVE_LOCAL_MONGOHOUSE to 1
#   and set the environment variable LOCAL_MONGOHOUSE_DIR to the root directory of the
#   mongohouse source tree.
# - To skip the operations of this script, set the environment variable SKIP_RUN_ADL to 1.

NAME=`basename "$0"`
if [[ $SKIP_RUN_ADL -eq 1 ]]; then
  echo "Skipping $NAME"
  exit 0
fi

ARG=`echo $1 | tr '[:upper:]' '[:lower:]'`
if [[ -z $ARG ]]; then
  echo "Usage: $NAME <operation>"
  echo "operation: 'start' or 'stop'"
  exit 0
fi

GO_VERSION="go1.16"
if [ -d "/opt/golang/$GO_VERSION" ]; then
  GOROOT="/opt/golang/$GO_VERSION"
  GOBINDIR="$GOROOT"/bin
  PATH=$GOBINDIR:$PATH
fi

TMP_DIR="/tmp/run_adl/"
MONGOHOUSE_URI=git@github.com:10gen/mongohouse.git
MONGOHOUSE_DIR=$(pwd)/mongohouse
MONGO_DB_PATH=$(pwd)/test_db
LOGS_PATH=$(pwd)/logs
DB_CONFIG_PATH=$(pwd)/resources/integration_test/testdata/adl_db_config.json
MONGOD_PORT=28017
MONGOHOUSED_PORT=27017
START="start"
STOP="stop"
MONGOD="mongod"
MONGOHOUSED="mongohoused"
TENANT_CONFIG="./testdata/config/mongodb_local/tenant-config.json"
MONGO_DOWNLOAD_LINK=
OS=$(uname)
TIMEOUT=120

MONGO_DOWNLOAD_BASE=https://fastdl.mongodb.org
# Ubuntu 18.04
MONGO_DOWNLOAD_UBUNTU=mongodb-linux-x86_64-ubuntu1804-5.0.4.tgz
# RedHat 7
MONGO_DOWNLOAD_REDHAT=mongodb-linux-x86_64-rhel70-5.0.4.tgz
# macOS
MONGO_DOWNLOAD_MAC=mongodb-macos-x86_64-5.0.4.tgz

check_procname() {
  ps -ef 2>/dev/null | grep $1 | grep -v grep >/dev/null 
  result=$?
  
  if [[ result -eq 0 ]]; then
    return 0
  else 
    return 1
  fi
}

check_port() {
  netstat -van 2>/dev/null | grep LISTEN | grep $1 >/dev/null
  result=$?
  
  if [[ result -eq 0 ]]; then
    return 0
  else 
    return 1
  fi
}

check_mongod() {
  check_procname $MONGOD
  process_check_result=$?
  check_port $MONGOD_PORT
  port_check_result=$?

  if [[ $process_check_result -eq 0 ]] && [[ $port_check_result -eq 0 ]]; then
    return 0
  else
    return 1
  fi
}

# Check if jq exists.  If not, download and set path
get_jq() {
  which jq
  if [[ $? -ne 0 ]]; then
    if [ $OS = "Linux" ]; then
      curl -L -o $TMP_DIR/jq https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64
    else
      curl -L -o $TMP_DIR/jq https://github.com/stedolan/jq/releases/download/jq-1.6/jq-osx-amd64
    fi
    chmod +x $TMP_DIR/jq
    export PATH=$PATH:$TMP_DIR
  fi
}

check_mongohoused() {
  check_procname $MONGOHOUSED
  process_check_result=$?
  check_port $MONGOHOUSED_PORT
  port_check_result=$?

  if [[ $process_check_result -eq 0 ]] && [[ $port_check_result -eq 0 ]]; then
    return 0
  else
    return 1
  fi
}

# check if mac or linux
if [ $OS = "Linux" ]; then
  distro=$(awk -F= '/^NAME/{print $2}' /etc/os-release)
  if [ "$distro" = "\"Red Hat Enterprise Linux\"" ] ||
[ "$distro" = "\"Red Hat Enterprise Linux Server\"" ]; then
    export VARIANT=rhel7
    MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_REDHAT
  elif [ "$distro" = "\"Ubuntu\"" ]; then
    export VARIANT=ubuntu1804  
    MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_UBUNTU
  else
    echo ${distro} not supported
    exit 1
  fi
elif [ $OS = "Darwin" ]; then
  MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_MAC
else
  echo $(uname) not supported
  exit 1
fi

check_mongod
if [[ $? -ne 0 ]]; then
  if [ $ARG = $START ]; then
    echo "Starting $MONGOD"
    # Install and start mongod
    if [ $OS = "Linux" ]; then
      curl -O $MONGO_DOWNLOAD_BASE/linux/$MONGO_DOWNLOAD_LINK
    else
      curl -O $MONGO_DOWNLOAD_BASE/osx/$MONGO_DOWNLOAD_LINK
    fi

    tar zxvf $MONGO_DOWNLOAD_LINK

    mkdir -p $MONGO_DB_PATH
    mkdir -p $LOGS_PATH
    mkdir -p $TMP_DIR

    # Note: ADL has a storage.json file that generates configs for us.
    # The mongodb source is on port $MONGOD_PORT so we use that here.
    ${MONGO_DOWNLOAD_LINK:0:$((${#MONGO_DOWNLOAD_LINK} - 4))}/bin/mongod --port $MONGOD_PORT --dbpath $MONGO_DB_PATH \
      --logpath $LOGS_PATH/mongodb_test.log --pidfilepath $TMP_DIR/${MONGOD}.pid --fork
  fi
else
  if [ $ARG = $STOP ]; then
    MONGOD_PID=$(< $TMP_DIR/${MONGOD}.pid)
    echo "Stopping $MONGOD, pid $MONGOD_PID"
    kill "$(< $TMP_DIR/${MONGOD}.pid)"
  fi
fi

check_mongohoused
if [[ $? -ne 0 ]]; then
  if [ $ARG = $START ]; then
    echo "Starting $MONGOHOUSED"
    if [[ $HAVE_LOCAL_MONGOHOUSE -eq 1 ]]; then
        if [ ! -d "$LOCAL_MONGOHOUSE_DIR" ]; then
            echo "ERROR: $LOCAL_MONGOHOUSE_DIR is not a directory"
            exit 1
        fi
        cd $LOCAL_MONGOHOUSE_DIR
    else
        echo "Downloading mongohouse"
        # Install and start mongohoused
        git config --global url.git@github.com:.insteadOf https://github.com/
        # Clone the mongohouse repo
        if [ ! -d "$MONGOHOUSE_DIR" ]; then
            git clone $MONGOHOUSE_URI
        fi
        cd $MONGOHOUSE_DIR
        git pull $MONGOHOUSE_URI

        export GOPRIVATE=github.com/10gen
        go mod download
    fi

    # Set relevant environment variables
    export MONGOHOUSE_ENVIRONMENT="local"
    export MONGOHOUSE_MQLRUN="$(pwd)/artifacts/mqlrun"
    export LIBRARY_PATH="$(pwd)/artifacts"

    # Download latest versions of external dependencies
    rm -f $MONGOHOUSE_MQLRUN
    go run cmd/buildscript/build.go tools:download:mqlrun
    rm -f $(pwd)/artifacts/libmongosql.a
    go run cmd/buildscript/build.go tools:download:mongosql

    get_jq
    # Load tenant config into mongodb
    STORES='{ "name" : "localmongo", "provider" : "mongodb", "uri" : "mongodb://localhost:%s" }'
    STORES=$(printf "$STORES" "${MONGOD_PORT}")
    DATABASES=$(cat $DB_CONFIG_PATH)
    # Replace the existing storage config with a wildcard collection for the local mongodb
    cp ${TENANT_CONFIG} ${TENANT_CONFIG}.orig
    jq "del(.storage)" ${TENANT_CONFIG} > ${TENANT_CONFIG}.tmp && mv ${TENANT_CONFIG}.tmp ${TENANT_CONFIG}
    jq --argjson obj "$STORES" '.storage.stores += [$obj]' ${TENANT_CONFIG} > ${TENANT_CONFIG}.tmp\
                                                               && mv ${TENANT_CONFIG}.tmp ${TENANT_CONFIG}
    jq --argjson obj "$DATABASES" '.storage.databases += $obj' ${TENANT_CONFIG} > ${TENANT_CONFIG}.tmp\
                                                               && mv ${TENANT_CONFIG}.tmp ${TENANT_CONFIG}

    go run cmd/buildscript/build.go init:mongodb-tenant

    mkdir -p $TMP_DIR
    mkdir -p $LOGS_PATH
    # Start mongohoused with appropriate config
    nohup go run -tags mongosql ./cmd/mongohoused/mongohoused.go \
      --config ./testdata/config/mongodb_local/frontend-agent-backend.yaml >> $LOGS_PATH/${MONGOHOUSED}.log &
    echo $! > $TMP_DIR/${MONGOHOUSED}.pid

    waitCounter=0
    while : ; do
        check_mongohoused
        if [[ $? -eq 0 ]]; then
            break
        fi
        if [[ "$waitCounter" -gt $TIMEOUT ]]; then
            echo "ERROR: Local ADL did not start under $TIMEOUT seconds"
            exit 1
        fi
        let waitCounter=waitCounter+1
        sleep 1
    done
  fi
else
  if [ $ARG = $STOP ]; then
    MONGOHOUSED_PID=$(< $TMP_DIR/${MONGOHOUSED}.pid)
    echo "Stopping $MONGOHOUSED, pid $MONGOHOUSED_PID"
    pkill -TERM -P ${MONGOHOUSED_PID}
    if [[ $HAVE_LOCAL_MONGOHOUSE -eq 1 && -d "$LOCAL_MONGOHOUSE_DIR" ]]; then
        echo "Restoring ${TENANT_CONFIG}"
        cd $LOCAL_MONGOHOUSE_DIR
        cp ${TENANT_CONFIG}.orig ${TENANT_CONFIG}
    fi
  fi
fi
