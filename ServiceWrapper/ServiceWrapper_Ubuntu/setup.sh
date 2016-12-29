#! /bin/sh

echo "change ./launch mod to execute"
chmod 755 ./launch
sudo dos2unix ./launch

echo "change ./shutDown.sh mod to execute"
chmod 755 ./shutDown.sh
sudo dos2unix ./shutDown.sh

echo "change ./remove.sh mod to execute"
chmod 755 ./remove.sh
sudo dos2unix ./remove.sh

echo "change ./ezScrum mod to execute"
chmod 755 ./ezScrum
sudo dos2unix ./ezScrum

echo "change ./wrapper mod to execute"
chmod 755 ./wrapper
sudo dos2unix ./wrapper

echo "Start to install ezScrum Application"
sudo ./ezScrum install


echo "finish"

