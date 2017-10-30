#!/bin/bash
{
  echo $1
  echo $2
} >changelog.txt
FILE='changelog.txt'
IP='vhundef.net'
USER='admin_vh'
PASS='Vhn323884489'
DESTINATION='public_html'
ftp -p -d -inv $IP<< !EOF
user $USER $PASS
cd $DESTINATION
asc
mput $FILE
quit
!EOF
