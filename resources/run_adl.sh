#!/bin/bash
MONGOHOUSE_URI=git@github.com:10gen/mongohouse.git
MONGOHOUSE_DIR=$(pwd)/mongohouse
GOVERSION=$(curl "https://go.dev/VERSION?m=text")
GO_DIR=$(pwd)/$GOVERSION

MONGO_DB_PATH=$(pwd)/test_db
MONGO_LOGS_PATH=$(pwd)/logs

MONGO_DOWNLOAD_BASE=https://fastdl.mongodb.org
# Ubuntu 18.04
MONGO_DOWNLOAD_UBUNTU=mongodb-linux-x86_64-ubuntu1804-5.0.4.tgz
# RedHat 8
MONGO_DOWNLOAD_REDHAT=mongodb-linux-x86_64-rhel80-5.0.4.tgz
# SLES 15
MONGO_DOWNLOAD_SLES=mongodb-linux-x86_64-suse15-5.0.4.tgz
MONGO_DOWNLOAD_MAC=mongodb-macos-x86_64-5.0.4.tgz

MONGOD_PORT=28017
MONGOHOUSED_PORT=27017

MONGO_DOWNLOAD_LINK=
OS=$(uname)

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
  netstat -vanp tcp 2>/dev/null | grep LISTEN | grep $1 >/dev/null
  result=$?
  
  if [[ result -eq 0 ]]; then
    return 0
  else 
    return 1
  fi
  
}

check_mongod() {
  MONGOD="mongod"
  
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

check_mongohoused() {
  MONGOHOUSED="mongohoused"
  
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
  if [ "$distro" = "\"Red Hat Enterprise Linux\"" ]; then
    MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_REDHAT
  elif [ "$distro" = "\"Ubuntu\"" ]; then
    MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_UBUNTU
  elif [ "$distro" = "\"SLES\"" ]; then
    MONGO_DOWNLOAD_LINK=$MONGO_DOWNLOAD_SLES
  else
    echo $(distro) not supported
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
  # Install and start mongod
  if [ $OS = "Linux" ]; then
    curl -O $MONGO_DOWNLOAD_BASE/linux/$MONGO_DOWNLOAD_LINK
  else
    curl -O $MONGO_DOWNLOAD_BASE/osx/$MONGO_DOWNLOAD_LINK
  fi
  
  tar zxvf $MONGO_DOWNLOAD_LINK
  
  mkdir -p $MONGO_DB_PATH
  mkdir -p $MONGO_LOGS_PATH

  # Note: ADL has a storage.json file that generates configs for us. The mongodb source is on port 28017 so we use that here.
  ${MONGO_DOWNLOAD_LINK:0:$((${#MONGO_DOWNLOAD_LINK} - 4))}/bin/mongod --port $MONGOD_PORT --dbpath $MONGO_DB_PATH --logpath $MONGO_LOGS_PATH/test.log --fork
fi

check_mongohoused
if [[ $? -ne 0 ]]; then
  # Install and start mongohoused 
  git config --global url.git@github.com:.insteadOf https://github.com/
  # Clone the mongohouse repo
  if [ ! -d "$MONGOHOUSE_DIR" ]; then
    git clone $MONGOHOUSE_URI
  fi
  cd $MONGOHOUSE_DIR
  git pull $MONGOHOUSE_URI
  
  OS_LOWERCASE=$(echo $OS | tr '[:upper:]' '[:lower:]')
  curl -O "https://dl.google.com/go/${GOVERSION}.${OS_LOWERCASE}-amd64.tar.gz"
  mkdir $GO_DIR
  tar -C $GO_DIR -xzf ${GOVERSION}.${OS_LOWERCASE}-amd64.tar.gz
  export PATH=$GO_DIR/go/bin:$PATH
  
  go mod vendor

  # Download external dependencies
  go run cmd/buildscript/build.go tools:download:mqlrun
  go run cmd/buildscript/build.go tools:download:mongosql
  
  # Set relevant environment variables
  export GOPRIVATE=github.com/10gen
  export MONGOHOUSE_ENVIRONMENT="local"
  export MONGOHOUSE_MQLRUN="$(pwd)/artifacts/mqlrun"
  export LIBRARY_PATH="$(pwd)/artifacts"
  
  # Load tenant config into mongodb
  # TODO - we should probably make a different tenant config than the one that exists at
  #  ./testdata/config/mongodb_local/tenant-config.json since that one includes a ton of extra datasources
  #  we not only don't need but likely don't want. We'll probably want a tenant config that has a wildcard
  #  so we can have any databases and collections in the local mongod.
  #  - We'll probably want the test runner, as opposed to this script, to be able to say what data should get
  #    restored.
  go run cmd/buildscript/build.go init:mongodb-tenant 
  
  # Start mongohoused with appropriate config
  go run -tags mongosql ./cmd/mongohoused/mongohoused.go --config ./testdata/config/mongodb_local/frontend-agent-backend.yaml
fi
