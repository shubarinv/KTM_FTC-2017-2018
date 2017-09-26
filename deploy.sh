#!/bin/bash

echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
FILEtoPut='TeamCode-debug.apk'

ftp -n files.000webhost.com <<END_SCRIPT
quote USER vhundef
quote PASS Vhn323884489
lcd TeamCode/build/outputs/apk
cd /public_html/lastest
put $FILEtoPut

quit
END_SCRIPT
exit 0
