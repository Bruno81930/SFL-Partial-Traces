#!/bin/bash

function match() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  OUTPUT="output/execution/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  #for PERCENTAGE in $(seq 0 10 100); do
    PERCENTAGE=50;
    NAME="execution_${PROJECT}_${BUG}_${PERCENTAGE}";
    COMMAND="module load anaconda && source activate logs_diagnosis && java -Xmx25G -jar logs.jar execution -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST -f RANDOM -t 100"
    sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output=/dev/null --error="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND";
    echo "$COMMAND";
    sleep 0.1;
  #done

  sleep 0.2;
}

function run_project() {
  local project_name=$1
  local versions=$(jq -r --arg pj "$project_name" '.[$pj][]' "$JSON_FILE")

  for version in $versions; do
      run_version "$project_name" "$version"
  done
}

function run_version() {
  local project_name=$1
  local version=$2
  match "$project_name" "$version" "src" "test"
}

ARG_PROJECT=$1;
ARG_VERSION=$2;


JSON_FILE="all.json"
if [ "$ARG_PROJECT" = "all" ]; then
    for project in $(jq -r 'keys[]' "$JSON_FILE"); do
      run_project "$project";
    done
else
    if [ "$ARG_VERSION" != "" ]; then
      run_version "$ARG_PROJECT" "$ARG_VERSION"
    else
      run_project "$ARG_PROJECT"
    fi
fi

    
