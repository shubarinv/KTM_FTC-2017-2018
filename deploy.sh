#!/bin/bash
echo $GCLOUD_SERVICE_KEY | base64 --decode --ignore-garbage > ${HOME}/gcloud-service-key.json
sudo /opt/google-cloud-sdk/bin/gcloud --quiet components update
sudo /opt/google-cloud-sdk/bin/gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json
sudo /opt/google-cloud-sdk/bin/gcloud config set project $GCLOUD_PROJECT
sudo /opt/google-cloud-sdk/bin/gcloud gsutil cp TeamCode/build/outputs/apk gs://vh-ftc/
