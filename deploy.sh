#!/bin/bash
sudo apt-get update && sudo apt-get install google-cloud-sdk
echo $GCLOUD_SERVICE_KEY | base64 --decode --ignore-garbage > ${HOME}/gcloud-service-key.json
sudo gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json
sudo gcloud config set project $GCLOUD_PROJECT
sudo gcloud gsutil cp TeamCode/build/outputs/apk gs://vh-ftc/
