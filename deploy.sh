#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
filename="TeamCode-debug.apk"
hostname="files.000webhost.com"
username="vhundef"
password="Vhn323884489"
ftp $hostname <<EOF
quote USER $username
quote PASS $password
lcd TeamCode/build/outputs/apk
cd /public_html/lastest
binary
put $filename
quit
EOF
