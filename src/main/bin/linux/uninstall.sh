#! /bin/bash


APP_NAME="JProxy"
installPath="/usr/local/jproxy"
echo  "Uninstalling $APP_NAME..."
cd "$installPath/wrapper/bin/"
if [ -f $installPath/wrapper/bin/cloudGateway.sh ];then
    bash $installPath/wrapper/bin/cloudGateway.sh remove
else
    echo "$APP_NAME already stopped."
fi
rm -rf $installPath
echo "Uninstall $APP_NAME success"
