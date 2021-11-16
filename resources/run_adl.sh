# 1. set git to use ssh instead of https
git config --global url.git@github.com:.insteadOf https://github.com/

# 2. clone the mongohouse repo
# TODO - where to put it
git clone git@github.com:10gen/mongohouse.git
cd mongohouse

# 3. download external dependencies
# TODO - make sure this works on evergreen
go run cmd/buildscript/build.go tools:download:mqlrun

# TODO - make sure this works on evergreen, also how to do this platform-specific
go run cmd/buildscript/build.go tools:download:mongosql

# 4. set relevant environment variables
export GOPRIVATE = github.com/10gen
export MONGOHOUSE_ENVIRONMENT = "local"
export MONGOHOUSE_MQLRUN = "$(pwd)/artifacts/mqlrun"
export LIBRARY_PATH="$(pwd)/artifacts"

# 5. download mongod
# TODO - what platform? or should it be all platforms? parameterize this
#   - this is an example platform
curl -O https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-5.0.3.tgz
tar zxvf mongodb-linux-x86_64-rhel70-5.0.3.tgz

# 6. start mongod
# Note: ADL has a storage.json file that generates configs for us. The mongodb source is on port 28017 so we use that here.
# TODO - parameterize dbpath and log output
mkdir -p test_db
mkdir -p logs
mongodb-linux-x86_64-rhel70-5.0.3/bin/mongod --port 28017 --dbpath test_db --logpath logs/test.log --fork

# 7. load tenant config into mongodb
# TODO - we should probably make a different tenant config than the one that exists at
#  ./testdata/config/mongodb_local/tenant-config.json since that one includes a ton of extra datasources
#  we not only don't need but likely don't want. We'll probably want a tenant config that has a wildcard
#  so we can have any databases and collections in the local mongod.
#  - We'll probably want the test runner, as opposed to this script, to be able to say what data should get
#    restored.
go run cmd/buildscript/build.go init:mongodb-tenant # TODO - will need different target for this if we use a different tenant-config

# 8. start mongohoused with appropriate config
go run -tags mongosql ./cmd/mongohoused/mongohoused.go --config ./testdata/config/mongodb_local/frontend-agent-backend.yaml
