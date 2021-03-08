#! /bin/bash

VM_NAME=sigfox2lo

gcloud compute firewall-rules delete --quiet ${VM_NAME}-www 
gcloud compute instances delete --quiet --zone=europe-west6-a ${VM_NAME}
