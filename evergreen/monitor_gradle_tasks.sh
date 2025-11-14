#!/bin/bash
set -e

GRADLE_PID=$1

echo "Gradle process started with PID $GRADLE_PID"

# On Amazon Linux 2 hosts, the gradlew integrationTest command was hanging indefinitely.
# This monitoring approach will detect build completion or failure even when the Gradle
# process doesn't terminate properly and allows the task to complete.

SECONDS=0
TIMEOUT=1800  # 30 minute timeout

while true; do
  if [ -f gradle_output.log ]; then
    if grep -q "BUILD SUCCESSFUL" gradle_output.log 2>/dev/null; then
        echo "Build successful!"
        EXITCODE=0
        break
    fi
    if grep -q "BUILD FAILED" gradle_output.log 2>/dev/null; then
        echo "Build failed!"
        EXITCODE=1
        break
    fi
  fi

  if (( SECONDS > TIMEOUT )); then
      echo "$TIMEOUT second timeout reached. Exiting with failure."
      EXITCODE=1
      break
  fi

  # Check if Gradle process is still running
  if ! kill -0 $GRADLE_PID 2>/dev/null; then
      echo "Gradle process has finished."
      wait $GRADLE_PID
      EXITCODE=$?
      break
  fi

  sleep 5
done

cat gradle_output.log

kill $GRADLE_PID 2>/dev/null || true

exit $EXITCODE
