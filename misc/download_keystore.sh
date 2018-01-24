#!/usr/bin/env bash
if [[ $ANDROID_KEY_STORE_FILE && ${ANDROID_KEY_STORE_FILE} && $ANDROID_KEY_STORE_FILE_URL && ${ANDROID_KEY_STORE_FILE_URL} ]]
then
    echo "Google Service detected - downloading..."
    mkdir -p ${ANDROID_KEY_STORE_FILE}
    curl -L -o ${ANDROID_KEY_STORE_FILE} ${ANDROID_KEY_STORE_FILE_URL}
    ls ~/repo
else
    echo "Google Service uri not set."
fi