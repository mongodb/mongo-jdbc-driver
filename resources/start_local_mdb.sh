#!/bin/bash
#
# Usage start_local_mdb.sh <community version> <enterprise version> <architecture>
# architecture: "arm64" or "x64"
#
# This script will download each version of mongodb, start a mongod
# for each, and create a user for each.

# Usage: download_and_extract_tgz <root url> <file base name>
download_and_extract_tgz() {
  tgz_file="$2.tgz"
  full_url="$1$tgz_file"

  echo "downloading $3 from $full_url"
  curl -O $full_url

  echo "extracting $tgz_file"
  tar zxvf $tgz_file
}

# Usage: download_mongod <download_host> <file base name> <type>
# type: "community" or "enterprise"
download_mongod() {
  root_url="https://$1/linux/"
  download_and_extract_tgz $root_url $2 $3
}

# Usage: download_mongosh <architecture>
# architecture: "arm64" or "x64"
download_mongosh() {
  root_url="https://downloads.mongodb.com/compass/"
  file_name="mongosh-2.3.0-linux-$1"
  download_and_extract_tgz $root_url $file_name "mongosh"

  # copy mongosh to the current directory and ensure it is executable
  cp $file_name/bin/mongosh .
  chmod +x mongosh
}

# Usage: start_mdb_and_create_user <type> <port> <user> <pwd> <mongod dir>
# type: "community" or "enterprise"
start_mdb_and_create_user() {
  echo "starting mongodb $1 on port $2"
  db_path="$1_db"
  mkdir -p $db_path
  $5/bin/mongod --dbpath $db_path --port $2 &

  echo "creating user"
  ./mongosh test --eval "db.createUser({user: '$3', pwd: '$4', roles: ['readWrite']})"
}

community_mdb_version="$1"
enterprise_mdb_version="$2"
arch="$3"

community_base_url="fastdl.mongodb.org"
enterprise_base_url="downloads.mongodb.com"

download_mongod $community_base_url $community_mdb_version "community"
download_mongod $enterprise_base_url $enterprise_mdb_version "enterprise"

download_mongosh $arch

start_mdb_and_create_user "community" $LOCAL_MDB_PORT_COM $LOCAL_MDB_USER $LOCAL_MDB_PWD $community_mdb_version
start_mdb_and_create_user "enterprise" $LOCAL_MDB_PORT_ENT $LOCAL_MDB_USER $LOCAL_MDB_PWD $enterprise_mdb_version
