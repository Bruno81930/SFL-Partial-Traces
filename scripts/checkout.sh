PROJECT=$1
VERSION=$2
OUTPUT=$3
defects4j checkout -p "${PROJECT}" -v "${VERSION}b" -w "${OUTPUT}/${PROJECT}/${VERSION}"
