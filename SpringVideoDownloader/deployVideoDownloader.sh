#!/bin/bash
cd /home/jonathan/git/SpringVideoDownloader/SpringVideoDownloader
mvn clean package
cd target
echo "Stopping videoDownloader"
sudo systemctl stop videoDownloader
cp SpringVideoDownloader-0.0.1-SNAPSHOT.jar /home/jonathan/videoDownloader/SpringVideoDownloader.jar
echo "Starting videoDownloader"
sudo systemctl start videoDownloader


# cd /home/jonathan/videoDownloader
# sudo java -jar -Xms1024M -Xmx4096M SpringVideoDownloader-0.0.1-SNAPSHOT.jar
