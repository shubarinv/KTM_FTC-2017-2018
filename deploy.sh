#!/bin/sh
echo "--------Installing ftp------"
sudo apt-get install ftp
echo "+++++++++ Ftp installed +++++++++++"
HOST='files.000webhost.com'
USER='vhundef'
PASSWD='Vhn323884489'
echo "Trying to open apk"
FILE='TeamCode/build/outputs/apk/TeamCode-debug.apk'
echo "$FILE"
ftp -inv $HOST <<EOF
user $USER $PASSWD
prompt
cd /public_html/lastest
put $FILE
quit
END_SCRIPT
exit 0
