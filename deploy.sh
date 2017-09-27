#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILE='TeamCode-debug.apk'
IP='files.000webhost.com'
USER='vhundef'
PASS='Vhn323884489'
DESTINATION='public_html/lastest'
ftp -inv $IP<< !EOF
user $USER $PASS
lcd TeamCode/build/outputs/apk
cd $DESTINATION
prompt
asc
put $FILE
quit
!EOF
