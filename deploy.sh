#!/bin/bash
sudo apt-get update && sudo apt-get install google-cloud-sdk
echo $GOOGLE_AUTH_JSON > vh-gcloud.json
gcloud auth activate-service-account --key-file=vh-gcloud.json
sudo gcloud config set project $GCLOUD_PROJECT
sudo gsutil cp TeamCode/build/outputs/apk/TeamCode-debug.apk gs://vh-ftc/
