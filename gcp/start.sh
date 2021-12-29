#! /bin/bash

VM_NAME=sigfox2lo
BUCKET_NAME=sigfox2lo
ZIP_NAME=sigfox2lo-1.1-SNAPSHOT.zip

mvn clean package -Prelease -f ../pom.xml

gsutil mb gs://${BUCKET_NAME}
gsutil cp ../target/${ZIP_NAME} gs://${BUCKET_NAME}/${ZIP_NAME}

gcloud compute firewall-rules create ${VM_NAME}-www --network default --allow tcp:8080 --target-tags ${VM_NAME}

gcloud compute instances create ${VM_NAME} \
  --tags ${VM_NAME} \
  --zone europe-west6-a  --machine-type n1-standard-1 \
  --metadata-from-file startup-script=install.sh