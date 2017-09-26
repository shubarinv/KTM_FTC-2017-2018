#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
sudo apt-get install putty
echo "+++++++++ Ftp installed +++++++++++"
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
echo "Trying to open apk"
psftp files.000webhost.com -l vhundef -pw Vhn323884489
lcd TeamCode/build/outputs/apk
cd /public_html/lastest
put TeamCode-debug.apk
quit
exit;
EOF
