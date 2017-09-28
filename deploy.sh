#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER='admin_vh'
PASS='Vhn323884489'
DESTINATION='public_html'
ftp -d -inv $IP<< !EOF
user $USER $PASS
lcd TeamCode/build/outputs/apk
cd $DESTINATION
asc
mput $FILE
quit
!EOF
