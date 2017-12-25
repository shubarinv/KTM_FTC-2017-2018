#!/bin/bash

echo $2 '\n' $3 >Build.info
echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER=$4
PASS=$5
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
cd $DESTINATION
asc
mput Build.info
asc
mput CHANGELOG.md
lcd TeamCode/build/outputs/apk
mput $FILE
rename $FILE $1-$FILE
quit
!EOF
