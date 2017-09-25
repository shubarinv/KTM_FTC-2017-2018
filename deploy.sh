#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
sudo apt-get install sshpass
echo "+++++++++ Ftp installed +++++++++++"
sudo sshpass -p 'Vhn323884489' TeamCode/build/outputs/apk/TeamCode-debug.apk vhundef@files.000webhost.com:/public_html/lastest
quit
END_SCRIPT
exit 0
#!/bin/bash
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'

ftp $HOST <<END_SCRIPT
user $USER $PASSWD
cd /public_html/lastest
put TeamCode/build/outputs/apk/TeamCode-debug.apk
quit
END_SCRIPT

exit 0
