#!/bin/bash

set -eu
set -x

# Run tests, script will stop running if tests fail
mvn clean test

WAR_FILE=./root-${RELEASE_VERSION}.war

# Pom version to given releaseversion
mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${RELEASE_VERSION}"

# Tag just before we're about to create the war file
git add pom.xml
git commit -m "Release ${RELEASE_VERSION}"
git tag "version-${RELEASE_VERSION}"
git push origin HEAD:refs/heads/master
git push origin version-${RELEASE_VERSION}

# Release
rm -rf target/root.war # ensure that the old war does not exist
rm -rf ${WAR_FILE} # double check
mvn clean verify -DskipTests -Dcommit.hash=$(git rev-parse HEAD)
mv target/root.war ${WAR_FILE}

# Next increment
mvn versions:set -DgenerateBackupPoms=false -DnewVersion="$NEXT_VERSION-SNAPSHOT"

git add pom.xml
git commit -m "Prepare ${NEXT_VERSION}-SNAPSHOT"
set +x
git push origin HEAD:refs/heads/master

echo "Transferring root.war to development environment..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "$WAR_FILE" "${SERVER}:${SERVER_BASE_DIR}/deploy-inbox/root-${RELEASE_VERSION}.war"
echo "Deploying war..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SERVER} -tt "${SERVER_BASE_DIR}/deploy/scripts/deploy.sh ${RELEASE_VERSION}"
echo "Done"