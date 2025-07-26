#!/bin/bash

function partial() {
  PROJECT=$1
  BUG=$2
  PERCENTAGE=$3
  OUTPUT="output/partial/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  NAME="partial_${PROJECT}_${BUG}_${PERCENTAGE}";
  COMMAND="module load anaconda && source activate logs_diagnosis && java -jar logs.jar partial -p $PROJECT -b $BUG -P $PERCENTAGE -d data -f RANDOM -t 100 -i 0"
  sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=15G --wrap "$COMMAND";
  echo "$COMMAND";
  sleep 0.2;
}

function sfl+() {
  PROJECT=$1
  BUG=$2
  PERCENTAGE=$3
  SOURCE=$4
  TEST=$5
  WALK=$6
  COMPUTATION=$7
  OUTPUT="output/sfl+/$PROJECT/$BUG/$WALK/$COMPUTATION";
  mkdir -p "$OUTPUT";

  ITERATION=0
  NAME="sfl+_${PROJECT}_${BUG}_${PERCENTAGE}_${ITERATION}";
  COMMAND="module load anaconda && source activate logs_diagnosis &&  java -Xmx27G -jar logs.jar sfl+ -p $PROJECT -b $BUG -P $PERCENTAGE -R 1000 -W $WALK -C $COMPUTATION -d data -s $SOURCE -s $TEST -f RANDOM -t 100 -i $ITERATION"
  sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}_${ITERATION}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND" --partition cpu;
  echo "$COMMAND";
  echo "Submitted $NAME."
  sleep 0.1;
  sleep 0.2;
}

function reconstructed() {
  PROJECT=$1
  BUG=$2
  SOURCE=$3
  TEST=$4
  OUTPUT="output/reconstructed/$PROJECT/$BUG";
  mkdir -p "$OUTPUT";

  for PERCENTAGE in $(seq 0 10 100); do
    NAME="reconstructed_${PROJECT}_${BUG}_${PERCENTAGE}";
    #for ITERATION in $(seq 0 100 1000); do
    ITERATION=0
      COMMAND="module load anaconda && source activate logs_diagnosis && java -Xmx25G -jar logs.jar reconstructed -p $PROJECT -b $BUG -P $PERCENTAGE -d data -s $SOURCE -s $TEST -f RANDOM -t 100 -i $ITERATION"
      sbatch -D /home/machadob/1_logs --mail-type=END,FAIL --mail-user=machadob@post.bgu.ac.il --job-name="$NAME" --output="${OUTPUT}/${PERCENTAGE}.txt" --cpus-per-task=120 --mem=30G --wrap "$COMMAND";
      echo "$COMMAND";
      sleep 0.1;
      #done;
  done

  sleep 0.2;

}

sfl+ jxpath 22 40 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 50 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 60 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 70 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 80 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 90 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
sfl+ jxpath 22 100 "src" "test" "INFORMED_SQUARED" "PROBABILITIES_HIT"
