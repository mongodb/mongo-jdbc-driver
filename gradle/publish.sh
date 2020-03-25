#!/bin/bash

# DO NOT ECHO COMMANDS AS THEY CONTAIN SECRETS!

set -o errexit  # Exit the script with error if any of the commands fail
set +o verbose # Command echoing off.
set +o xtrace # Disable command traces before executing them.

############################################
#            Main Program                  #
############################################
echo ${RING_FILE_GPG_BASE64} | base64 --decode > ${PROJECT_DIRECTORY}/secring.gpg

trap "rm ${PROJECT_DIRECTORY}/secring.gpg; exit" EXIT HUP

export ORG_GRADLE_PROJECT_nexus_username=${NEXUS_USERNAME}
export ORG_GRADLE_PROJECT_nexus_password=${NEXUS_PASSWORD}
export ORG_GRADLE_PROJECT_signing_key_id=${SIGNING_KEY_ID}
export ORG_GRADLE_PROJECT_signing_password=${SIGNING_PASSWORD}
export ORG_GRADLE_PROJECT_signing_secretKeyRingFile=${PROJECT_DIRECTORY}/secring.gpg
export ORG_GRADLE_PROJECT_nexus_url=${NEXUS_URL}
export ORG_GRADLE_PROJECT_nexus_profile_id=${NEXUS_PROFILE_ID}

echo "Publishing snapshot with jdk9"
export JAVA_HOME="/opt/java/jdk9"

./gradlew -version
./gradlew publishMaven --info
