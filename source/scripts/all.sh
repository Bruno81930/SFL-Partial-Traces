#!/bin/bash

function partial() {
  PROJECT=$1
  BUG=$2
  OUTPUT="output/partial/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  #for PERCENTAGE in $(seq 0 10 100); do
  PERCENTAGE=50
    NAME="partial_${PROJECT}_${BUG}_${PERCENTAGE}";
    #for ITERATION in $(seq 0 100 1000); do
    ITERATION=0
      COMMAND="module load anaconda && source activate logs_diagnosis && java -jar logs.jar partial -p $PROJECT -b $BUG -P $PERCENTAGE -d data -f RANDOM -t 100 -i $ITERATION"
      #sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=15G --wrap "$COMMAND";
      sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output=/dev/null --error="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=15G --wrap "$COMMAND";
      echo "$COMMAND";
      sleep 0.1;
    #done
  #done

  sleep 0.2;
}

function sfl+() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  WALK=$5
  COMPUTATION=$6
  OUTPUT="output/sfl+/$PROJECT/$BUG/$WALK/$COMPUTATION";
  mkdir -p "$OUTPUT";

  #for PERCENTAGE in $(seq 0 10 100); do
  PERCENTAGE=50;
    #for ITERATION in $(seq 0 50 100); do
    ITERATION=0
      NAME="sfl+_${PROJECT}_${BUG}_${PERCENTAGE}_${ITERATION}";
      COMMAND="module load anaconda && source activate logs_diagnosis &&  java -Xmx37G -jar logs.jar sfl+ -p $PROJECT -b $BUG -P $PERCENTAGE -R 1000 -W $WALK -C $COMPUTATION -d data -s $SOURCE -s $TEST -f RANDOM -t 100 -i $ITERATION"
      #sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}_${ITERATION}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND" --partition cpu;
      sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output=/dev/null --error="${OUTPUT}/${PERCENTAGE}_${ITERATION}.txt" --cpus-per-task=120 --mem=40G --wrap "$COMMAND" --partition cpu;
      echo "$COMMAND";
      echo "Submitted $NAME."
      sleep 0.1;
    #done
  #done
  sleep 0.2;
}

function reconstructed() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  OUTPUT="output/reconstructed/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  PERCENTAGE=50;
  #for PERCENTAGE in $(seq 0 10 100); do
    NAME="reconstructed_${PROJECT}_${BUG}_${PERCENTAGE}";
    #for ITERATION in $(seq 0 100 1000); do
    ITERATION=0
      COMMAND="module load anaconda && source activate logs_diagnosis && java -Xmx25G -jar logs.jar reconstructed -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST -f RANDOM -t 100 -i $ITERATION"
      #sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND";
      sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output=/dev/null --error="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND";
      echo "$COMMAND";
      sleep 0.1;
      #done;
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
 # partial "$project_name" "$version"
 # reconstructed "$project_name" "$version" "src" "test"
  #sfl+ "$project_name" "$version" "src" "test" "RANDOM" "PROBABILITIES"
  sfl+ "$project_name" "$version" "src" "test" "INFORMED" "PROBABILITIES"
  sfl+ "$project_name" "$version" "src" "test" "RANDOM_INFORMED" "PROBABILITIES"
  sfl+ "$project_name" "$version" "src" "test" "RANDOM_INFORMED_SQUARED" "PROBABILITIES"
  #sfl+ "$project_name" "$version" "src" "test" "RANDOM" "PROBABILITIES_HIT"
  #sfl+ "$project_name" "$version" "src" "test" "INFORMED" "PROBABILITIES_HIT"
  #sfl+ "$project_name" "$version" "src" "test" "RANDOM_INFORMED" "PROBABILITIES_HIT"
  #sfl+ "$project_name" "$version" "src" "test" "RANDOM_INFORMED_SQUARED" "PROBABILITIES_HIT"
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

    
