#!/usr/bin/env bash
if [[ $ANDROID_KEY_STORE_FILE && ${ANDROID_KEY_STORE_FILE} && $ANDROID_KEY_STORE_FILE_URL && ${ANDROID_KEY_STORE_FILE_URL} ]]
then
    echo "Google Service detected - downloading..."
    echo "File: " $ANDROID_KEY_STORE_FILE " URL: " $ANDROID_KEY_STORE_FILE_URL
    mkdir -p $ANDROID_KEY_STORE_FILE
    curl -L -o ${ANDROID_KEY_STORE_FILE} ${ANDROID_KEY_STORE_FILE_URL}
else
    echo "Google Service uri not set."
fi