#!/bin/bash
sudo apt-get update && sudo apt-get install google-cloud-sdk
gcloud auth activate-service-account --key-file=vh-gcloud.json
gcloud config set pass_credentials_to_gsutil true
sudo gcloud config set project vh-ktm
yes Y | sudo gcloud config set account vh-424@vh-ktm.iam.gserviceaccount.com
sudo gsutil config 
sudo gsutil cp TeamCode/build/outputs/apk/TeamCode-debug.apk gs://vh-ftc/
