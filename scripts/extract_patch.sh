#!/bin/bash

PROJECT=$1
VERSION=$2

cp /home/machadob/defects4j/framework/projects/$PROJECT/patches/$VERSION.src.patch ground_truth/$PROJECT/$VERSION/patch.txt
