#!/bin/bash

FILE='TeamCode-debug.apk'
IP='vhundef.net'
USER=$1
PASS=$2
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
cd $DESTINATION
asc
lcd TeamCode/build/outputs/apk
mput $FILE
rename $FILE $3-$FILE
quit
!EOF
