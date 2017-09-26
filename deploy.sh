#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
apt-get install putty
echo "+++++++++ Ftp installed +++++++++++"
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
echo "Trying to open apk"
psftp $HOST -l $USER -pw $PASSWD -v



lcd TeamCode/build/outputs/apk
cd /public_html/lastest
put TeamCode-debug.apk
quit
exit;
EOF
