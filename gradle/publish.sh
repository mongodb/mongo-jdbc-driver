#!/bin/bash

# DO NOT ECHO COMMANDS AS THEY CONTAIN SECRETS!

set -o errexit  # Exit the script with error if any of the commands fail
set +o verbose # Command echoing off.
set +o xtrace # Disable command traces before executing them.

############################################
#            Main Program                  #
############################################
echo "${RING_FILE_GPG_BASE64}" | base64 --decode >${PROJECT_DIRECTORY}/secring.gpg

trap "rm ${PROJECT_DIRECTORY}/secring.gpg; exit" EXIT HUP

export ORG_GRADLE_PROJECT_nexus_token_name=${NEXUS_TOKEN_NAME}
export ORG_GRADLE_PROJECT_nexus_token=${NEXUS_TOKEN}
export ORG_GRADLE_PROJECT_signing_key_id=${SIGNING_KEY_ID}
export ORG_GRADLE_PROJECT_signing_password=${SIGNING_PASSWORD}
export ORG_GRADLE_PROJECT_signing_secretKeyRingFile=${PROJECT_DIRECTORY}/secring.gpg
export ORG_GRADLE_PROJECT_nexus_url=${NEXUS_URL}
export ORG_GRADLE_PROJECT_nexus_profile_id=${NEXUS_PROFILE_ID}

set -o verbose # Command echoing on.
set -o xtrace # Enable command traces before executing them.
echo "Publishing snapshot or release"

./gradlew ${IS_RELEASE_PROP} -version
./gradlew ${IS_RELEASE_PROP} publishMaven --info
