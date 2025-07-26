#!/bin/bash
set -x;


function partial() {
  PROJECT=$1
  BUG=$2
  OUTPUT="output/partial/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  for PERCENTAGE in $(seq 0 10 100); do
    NAME="partial_${PROJECT}_${BUG}_${PERCENTAGE}";
    COMMAND="java -jar logs.jar partial -p $PROJECT -b $BUG -P $PERCENTAGE -d data -f RANDOM_WITH_FAULTS"
    sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=10 --mem=15G --wrap "$COMMAND";
  done

  sleep 0.2;
}

function partial_single() {
  PROJECT=$1
  BUG=$2
  PERCENTAGE=$3
  OUTPUT="output/partial/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  NAME="partial_${PROJECT}_${BUG}_${PERCENTAGE}";
  COMMAND="java -jar logs.jar partial -p $PROJECT -b $BUG -P $PERCENTAGE -d data -f RANDOM_WITH_FAULTS"
  sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=10 --mem=15G --wrap "$COMMAND";

  sleep 0.2;
}

function run_project() {
  local project_name=$1
  local versions=$(jq -r --arg pj "$project_name" '.[$pj][]' "$JSON_FILE")

  for version in $versions; do
      partial "$project_name" "$version"
  done
}

function run_version() {
  local project_name=$1
  local version=$2
  partial "$project_name" "$version"
}

ARG_PROJECT=$1;
ARG_VERSION=$2;
ARG_PERCENTAGE=$3;

JSON_FILE="partial.json"
if [ "$ARG_PROJECT" = "all" ]; then
  for project in $(jq -r 'keys[]' "$JSON_FILE"); do
    run_project "$project"
  done
else
    if [ "$ARG_PERCENTAGE" != "" ]; then
      partial_single "$ARG_PROJECT" "$ARG_VERSION" "$ARG_PERCENTAGE"
    elif [ "$ARG_VERSION" != "" ]; then
      run_version "$ARG_PROJECT" "$ARG_VERSION"
    else
      run_project "$ARG_PROJECT"
    fi
fi