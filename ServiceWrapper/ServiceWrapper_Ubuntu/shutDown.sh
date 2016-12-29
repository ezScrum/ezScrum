#! /bin/sh
ezScrumServisePID="$(ps aux | grep 'java -jar start.jar' | awk '{ print $2 }' | head -n 1)"
echo "$ezScrumServisePID"
sudo kill -9 "$ezScrumServisePID"