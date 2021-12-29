#! /bin/bash

## This startup script runs ON the compute vm

BUCKET_NAME=sigfox2lo
ZIP_NAME=sigfox2lo-1.1-SNAPSHOT.zip
DIR_NAME=sigfox2lo-1.1-SNAPSHOT

sudo su -
apt-get install unzip
apt-get install openjdk-11-jdk -y

mkdir /opt/sigfox2lo

gsutil cp gs://${BUCKET_NAME}/${ZIP_NAME} /opt/sigfox2lo/${ZIP_NAME}
unzip -q /opt/sigfox2lo/${ZIP_NAME} -d /opt/sigfox2lo/ 
./opt/sigfox2lo/${DIR_NAME}/bin/app.sh & 
exit