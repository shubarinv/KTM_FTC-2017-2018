#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
sudo apt-get install sshpass
echo "+++++++++ Ftp installed +++++++++++"
sshpass -p 'Vhn323884489' TeamCode/build/outputs/apk/TeamCode-debug.apk vhundef@files.000webhost.com:/public_html/lastest
quit
END_SCRIPT
exit 0
