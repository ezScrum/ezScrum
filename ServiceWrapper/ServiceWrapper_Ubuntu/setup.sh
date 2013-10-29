#! /bin/sh

echo "change ./ezScrum mod to execute"
chmod 755 ./ezScrum

echo "change ./wrapper mod to execute"
chmod 755 ./wrapper

echo "Start to install ezScrum Application"
sudo ./ezScrum install

echo "Start ezScrum service"
sudo ./ezScrum start

echo "finish"

