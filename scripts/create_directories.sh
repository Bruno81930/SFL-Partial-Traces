#!/bin/bash

PROJECT=$1
VERSION=$2

mkdir -p projects/$PROJECT/$VERSION
cp ground_truth/$PROJECT/$VERSION/ground_truth.txt projects/$PROJECT/$VERSION

