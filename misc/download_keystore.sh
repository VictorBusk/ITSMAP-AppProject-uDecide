#!/usr/bin/env bash
if [[ $ANDROID_KEY_STORE_FILE && ${ANDROID_KEY_STORE_FILE} && $ANDROID_KEY_STORE_FILE_URL && ${ANDROID_KEY_STORE_FILE_URL} ]]
then
    echo "Google Service detected - downloading..."
    curl -L --create-dirs --output ${ANDROID_KEY_STORE_FILE} ${ANDROID_KEY_STORE_FILE_URL}
else
    echo "Google Service uri not set."
fi