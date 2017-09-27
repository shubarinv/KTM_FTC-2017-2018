#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
FILEtoPut='TeamCode-debug.apk'

ftp -n $HOST <<END_SCRIPT
quote USER $USER
quote PASS $PASSWD
echo "+++++++++ Login OK +++++++++++"
lcd TeamCode/build/outputs/apk
cd /public_html/lastest
echo "+++++++++ Dir changed +++++++++++"
put $FILEtoPut
echo "+++++++++ File uploaded +++++++++++"
quit
END_CRIPT
echo "+++++++++ EOF +++++++++++"
exit 0
