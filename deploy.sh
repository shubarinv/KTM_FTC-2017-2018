#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER='admin_vh'
PASS='Vhn323884489'
FName=$1'-'$File
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
lcd TeamCode/build/outputs/apk
cd $DESTINATION
asc
mput $FILE
rename $FILE $FName
quit
!EOF
