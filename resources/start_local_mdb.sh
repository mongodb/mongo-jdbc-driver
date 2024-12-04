#!/bin/bash
#
# Usage start_local_mdb.sh <community version> <enterprise version> <architecture> [x509_cert_dir] [--x509]

# architecture: "arm64" or "x64"
#
# This script will download each version of mongodb, start a mongod
# for each, and create a user for each. With --x509 flag, it will
# set up X.509 authentication.

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

  echo "waiting 5 seconds to allow mongod to finish starting before connecting"
  sleep 5

  echo "creating user for $1"
  ./mongosh test --port $2 --eval "db.createUser({user: '$3', pwd: '$4', roles: ['readWrite']})"
}

# Usage: start_mdb_with_x509 <type> <port> <mongod dir> <$x509_cert_dir>
start_mdb_with_x509() {
  local type=$1
  local port=$2
  local mongod_dir=$3
  local x509_cert_dir="$4"
  local db_path="${type}_db"

  echo "Starting MongoDB $type with initial configuration on port $port"
  mkdir -p $db_path
  $mongod_dir/bin/mongod --dbpath $db_path --port $port &

  echo "Waiting 5 seconds for MongoDB to start..."
  sleep 5

  echo "Creating X.509 user..."
  ./mongosh --port $port \
    --eval 'db.getSiblingDB("$external").runCommand({
      createUser: "OU=eng,O=mongodb,L=NY,ST=NY,C=US",
      roles: [
        { role: "readWrite", db: "test" },
        { role: "userAdminAnyDatabase", db: "admin" }
      ]
    })'

  echo "Stopping MongoDB to restart with auth..."
  $mongod_dir/bin/mongod --dbpath $db_path --shutdown
  sleep 5

  echo "Starting MongoDB with X.509 authentication enabled..."
  $mongod_dir/bin/mongod \
    --dbpath $db_path \
    --port $port \
    --tlsMode requireTLS \
    --tlsCertificateKeyFile "$x509_cert_dir/server.pem" \
    --tlsCAFile "$x509_cert_dir/ca.crt" \
    --bind_ip localhost &

  echo "MongoDB started with X.509 authentication enabled on port $port"
}

# Parse command line arguments
community_mdb_version="$1"
enterprise_mdb_version="$2"
arch="$3"
x509_cert_dir="$4"
x509_mode=false

if [ "$5" = "--x509" ]; then
  x509_mode=true
fi

community_base_url="fastdl.mongodb.org"
enterprise_base_url="downloads.mongodb.com"

download_mongod $community_base_url $community_mdb_version "community"
download_mongod $enterprise_base_url $enterprise_mdb_version "enterprise"

download_mongosh $arch

if [ "$x509_mode" = true ]; then
  start_mdb_with_x509 "enterprise" $LOCAL_MDB_PORT_ENT $enterprise_mdb_version $x509_cert_dir
else
  start_mdb_and_create_user "community" $LOCAL_MDB_PORT_COM $LOCAL_MDB_USER $LOCAL_MDB_PWD $community_mdb_version
  start_mdb_and_create_user "enterprise" $LOCAL_MDB_PORT_ENT $LOCAL_MDB_USER $LOCAL_MDB_PWD $enterprise_mdb_version
fi
