#!/bin/sh
sudo apt-get install ftp
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
FILE='/home/circleci/code/TeamCode/build/outputs/apk/TeamCode-debug.apk'

ftp -n $HOST <<END_SCRIPT
quote USER $USER
quote PASS $PASSWD
put $FILE
quit
END_SCRIPT
exit 0
