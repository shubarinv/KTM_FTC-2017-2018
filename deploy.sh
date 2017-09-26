#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
echo "Trying to open apk"

ftp -inv -passive=on $HOST <<EOF
user $USER $PASSWD
prompt
quote epsv
lcd TeamCode/build/outputs/apk
cd /public_html/lastest
put TeamCode-debug.apk
quit
exit;
EOF
