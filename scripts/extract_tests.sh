#!/bin/bash
PROJECT=$1
VERSION=$2
DIRECTORY=$3/$1/$2
OUT_DIRECTORY=$4/$1/$2

FAILED_TESTS_PATH="${OUT_DIRECTORY}/failed_tests.txt"
ALL_TESTS_PATH="${OUT_DIRECTORY}/all_tests.txt"
REPO_PATH="${OUT_DIRECTORY}/repo"

if [ -s "$ALL_TESTS_PATH" ]; then
    echo "File $ALL_TESTS_PATH exists. Exiting the program."
    exit 1
fi

## Run tests
echo "Running tests";
defects4j test -w "${DIRECTORY}"

# Save trigger_test info
echo "Saving trigger_test info into the failed tests";
grep "d4j.tests.trigger" "${DIRECTORY}/defects4j.build.properties" | sed 's/d4j.tests.trigger=\(.*\)::\(.*\)/\1 \2/' > ${FAILED_TESTS_PATH}

# Save all_tests info
echo "Saving all_tests info";
awk -F'(' '{print $2" "$1}' "${DIRECTORY}/all_tests" | tr -ds ")" "" > ${ALL_TESTS_PATH}

echo "Get src and test paths";
src_path=$(grep "d4j.dir.src.classes" "${DIRECTORY}/defects4j.build.properties" | cut -d'=' -f2)
test_path=$(grep "d4j.dir.src.tests" "${DIRECTORY}/defects4j.build.properties" | cut -d'=' -f2)

mkdir -p ${REPO_PATH};
cp -r "${DIRECTORY}/${src_path}" "${REPO_PATH}/src"
cp -r "${DIRECTORY}/${test_path}" "${REPO_PATH}/test"
