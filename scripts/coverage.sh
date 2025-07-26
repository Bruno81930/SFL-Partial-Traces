#!/bin/bash

PROJECT=$1

commands=()

if [ "$PROJECT" = "Chart1" ]; then
	for i in 26 12 10 8 4; do
		commands+=("./extract_coverage.sh Chart $i sf projects");
	done
fi

if [ "$PROJECT" = "Chart2" ]; then
	for i in 24 13 11 7 5; do
		commands+=("./extract_coverage.sh Chart $i sf projects");
	done
fi

if [ "$PROJECT" = "Chart3" ]; then
	for i in 23 20 9 6 3; do
		commands+=("./extract_coverage.sh Chart $i sf projects");
	done
fi

if [ "$PROJECT" = "Cli" ]; then
	for i in 3 4 5 8 9 10 11 12 14 19 20 23 24 25 27 28 29 32 35 37 38 40; do
		commands+=("./extract_coverage.sh Cli $i sf projects");
	done;
fi

if [ "$PROJECT" = "Closure" ]; then
	for i in 1 2 5 7 11 13 14 15 17 18 19 20 23 24 25 28 31 32 33 36 38 39 40 42 44 50 52 53 56 57 58 59 61 62 65 66 67 69 70 71 73 77 78 81 82 83 86 91 92 94 96 97 99 101 102 104 105 107 111 112 113 115 116 117 118 119 120 121 122 123 126 128 129 130 131 132 146 151 152 160 164 176; do
		commands+=("./extract_coverage.sh Closure $i sf projects");
	done;
fi
if [ "$PROJECT" = "Codec" ]; then
	for i in 2 4 5 6 7 9 10 15 18; do
		commands+=("./extract_coverage.sh Codec $i sf projects");
	done;
fi

if [ "$PROJECT" = "Collections" ]; then
	for i in 26 28; do
		commands+=("./extract_coverage.sh Collections $i sf projects");
	done;
fi

if [ "$PROJECT" = "Compress" ]; then
	for i in 5 7 10 11 12 13 14 15 16 17 18 19 21 23 24 25 27 28 30 31 32 35 36 37 38 39 40 41 42 44 45; do
		commands+=("./extract_coverage.sh Compress $i sf projects");
	done;
fi

if [ "$PROJECT" = "Csv" ]; then
	for i in 2 3 4 5 6 7 9 10 11 13 14 15; do
		commands+=("./extract_coverage.sh Csv $i sf projects");
	done;
fi

if [ "$PROJECT" = "Gson" ]; then
	for i in 11 12 13 15 18; do
		commands+=("./extract_coverage.sh Gson $i sf projects");
	done;
fi

if [ "$PROJECT" = "JacksonCore" ]; then
	for i in 3 4 5 6 7 8 11 20 21 23 25 26; do
		commands+=("./extract_coverage.sh JacksonCore $i sf projects");
	done;	
fi

if [ "$PROJECT" = "JacksonDatabind" ]; then
	for i in 1 5 6 7 8 9 12 16 17 19 21 24 27 33 34 37 42 43 44 45 46 47 51 54 57 58 63 64 66 71 82 84 85 88 93 94 96 98 99 100 101 102 105 107 110 112; do
		commands+=("./extract_coverage.sh JacksonDatabind $i sf projects");
	done;
fi

if [ "$PROJECT" = "JacksonXml" ]; then
	for i in 3 5; do
		commands+=("./extract_coverage.sh JacksonXml $i sf projects");
	done;
fi

if [ "$PROJECT" = "Jsoup" ]; then
	for i in 2 5 6 9 10 14 19 20 26 27 29 31 32 34 38 39 42 43 45 46 47 49 50 51 53 54 57 61 62 64 68 69 70 72 76 77 78 79 80 82 83 84 85 86 88 89 90 93; do
		commands+=("./extract_coverage.sh Jsoup $i sf projects");
	done;
fi

if [ "$PROJECT" = "JxPath" ]; then
	for i in 5 6 10 12 15 18 21 22; do
		commands+=("./extract_coverage.sh JxPath $i sf projects");
	done;
fi

if [ "$PROJECT" = "Mockito" ]; then
	#for i in 1 8 9 12 15 18 29 32 33 34 36 38; do
	for i in 9; do
		commands+=("./extract_coverage.sh Mockito $i sf projects");
	done;
fi

if [ "$PROJECT" = "Time" ]; then
	for i in 4 5 7 8 10 11 14 16 17 18 19 20 23 24 25 27; do
		commands+=("./extract_coverage.sh Time $i sf projects");
	done;
fi

printf "%s\n" "${commands[@]}" | parallel;

