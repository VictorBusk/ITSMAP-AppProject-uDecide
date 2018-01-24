#!/usr/bin/env bash
if [[ $GOOGLE_SERVICE_FILE && ${GOOGLE_SERVICE_FILE} && $GOOGLE_SERVICE_FILE_URL && ${GOOGLE_SERVICE_FILE_URL} ]]
then
    echo "Keystore detected - downloading..."
    curl -L -o ${GOOGLE_SERVICE_FILE} ${GOOGLE_SERVICE_FILE_URL}
else
    echo "Keystore uri not set."
fi