#!/bin/bash
set -x;

PROJECT=$1;
JSON_FILE="query.json"

function query() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  OUTPUT="output/query/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  for PERCENTAGE in $(seq 10 10 90); do
    NAME="reconstructed_${PROJECT}_${BUG}_${PERCENTAGE}";
    COMMAND="java -Xmx25G -jar logs.jar query ground-truth -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST"
    sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --time="00:30:00" --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=10 --mem=30G --wrap "$COMMAND";
  done
}

function run_project() {
  local project_name=$1
  local versions=$(jq -r --arg pj "$project_name" '.[$pj][]' "$JSON_FILE")

  for version in $versions; do
    query "$project_name" "$version" "src" "test"
  done
}

if [ "$PROJECT" = "all" ]; then
  for project in $(jq -r 'keys[]' "$JSON_FILE"); do
    run_project "$project"
  done
else
  run_project "$PROJECT"
fi

    
