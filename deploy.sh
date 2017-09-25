#!/bin/bash
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
sudo apt-get install ftp
ftp $HOST <<END_SCRIPT
user $USER $PASSWD
prompt
cd /public_html/lastest
put TeamCode/build/outputs/apk/TeamCode-debug.apk
quit
END_SCRIPT

exit 0
