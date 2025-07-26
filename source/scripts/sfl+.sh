#!/bin/bash
set -x;



function sfl+() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  OUTPUT="output/sfl+/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  for PERCENTAGE in $(seq 0 10 100); do
    NAME="sfl+_${PROJECT}_${BUG}_${PERCENTAGE}";
    COMMAND="java -Xmx25G -jar logs.jar sfl+ -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST -f RANDOM_WITH_FAULTS"
    sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=10 --mem=30G --wrap "$COMMAND";
  done

  sleep 0.2;

}

function sfl+_single() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  PERCENTAGE=$5
  OUTPUT="output/sfl+/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  NAME="sfl+_${PROJECT}_${BUG}_${PERCENTAGE}";
  COMMAND="java -Xmx25G -jar logs.jar sfl+ -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST -f RANDOM_WITH_FAULTS"
  sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=10 --mem=30G --wrap "$COMMAND";

  sleep 0.2;
}


function run_project() {
  local project_name=$1
  local versions=$(jq -r --arg pj "$project_name" '.[$pj][]' "$JSON_FILE")

  for version in $versions; do
      sfl+ "$project_name" "$version" "src" "test"
  done
}

function run_version() {
  local project_name=$1
  local version=$2
  sfl+ "$project_name" "$version" "src" "test"
}

ARG_PROJECT=$1;
ARG_VERSION=$2;
ARG_PERCENTAGE=$3;

JSON_FILE="sfl+.json"
if [ "$ARG_PROJECT" = "all" ]; then
    for project in $(jq -r 'keys[]' "$JSON_FILE"); do
      run_project "$project";
    done
else
    if [ "$ARG_PERCENTAGE" != "" ]; then
      sfl+_single "$ARG_PROJECT" "$ARG_VERSION" "src" "test" "$ARG_PERCENTAGE"
    elif [ "$ARG_VERSION" != "" ]; then
      run_version "$ARG_PROJECT" "$ARG_VERSION"
    else
      run_project "$ARG_PROJECT"
    fi
fi

    
