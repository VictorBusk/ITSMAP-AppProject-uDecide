#!/usr/bin/env bash
if [[ $GOOGLE_SERVICE_FILE && ${GOOGLE_SERVICE_FILE} && $GOOGLE_SERVICE_FILE_URL && ${GOOGLE_SERVICE_FILE_URL} ]]
then
    echo "Keystore detected - downloading..."
    mkdir -p ${GOOGLE_SERVICE_FILE}
    curl -L -o ${GOOGLE_SERVICE_FILE} ${GOOGLE_SERVICE_FILE_URL}
    ls ~/repo/app
else
    echo "Keystore uri not set."
fi