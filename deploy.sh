#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER='admin_vh'
PASS='Vhn323884489'
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
cd $DESTINATION
asc
mput CHANGELOG.md
lcd TeamCode/build/outputs/apk
mput $FILE
rename $FILE $1-$FILE
quit
!EOF
