#!/bin/bash
echo $4 '\n' $5 >Build.info

FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER=$1
PASS=$2
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
cd $DESTINATION
asc
mput Build.info
asc
lcd TeamCode/build/outputs/apk
mput $FILE
rename $FILE $3-$FILE
quit
!EOF
