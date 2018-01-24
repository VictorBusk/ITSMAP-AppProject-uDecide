# use curl to download a keystore from $ANDROID_KEY_STORE_FILE_URL, if set,
# to the path/filename set in $KEYSTORE.
if [[ $KEYSTORE && ${KEYSTORE} && $ANDROID_KEY_STORE_FILE_URL && ${ANDROID_KEY_STORE_FILE_URL} ]]
then
    echo "Keystore detected - downloading..."
    # we're using curl instead of wget because it will not
    # expose the sensitive uri in the build logs:
    curl -L -o ${KEYSTORE} ${ANDROID_KEY_STORE_FILE_URL}
else
    echo "Keystore uri not set.  .APK artifact will not be signed."
fi