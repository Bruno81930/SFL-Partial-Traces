#!/bin/bash
PROJECT=$1
VERSION=$2
DIRECTORY=$3/$1/$2

OUTPUT_PATH="${DIRECTORY}/all_components.txt"
INPUT_PATH="${DIRECTORY}/repo/src"

java -jar extractClass.jar -i $INPUT_PATH -o $OUTPUT_PATH
