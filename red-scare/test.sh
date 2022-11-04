#!/bin/bash

function run {
  algoname=$1
  algofiles=$2
  file=$3
  outputfile="./results/$algoname-$file"

  if ! [[ -d './build' ]]; then
    mkdir './build'
  fi

  if ! [[ -d './results' ]]; then
    mkdir './results'
  fi

  if ! [[ -f "./build/$algoname" ]]; then
    echo "Compiling $algoname..."
    g++ -std=c++17 -O3 -o "./build/$algoname" $algofiles
  fi

  result=$(eval "./build/$algoname < ./data/$file")
  echo -e "$result\t$algoname\t$file" > $outputfile
  echo -e "$result\t$algoname\t$file"
}

algos=(\
  './src/AlternateAlgo.cpp' \
  './src/FewAlgo.cpp' \
  './src/SomeAlgo.cpp' \
  './src/ManyAlgo.cpp' \
  './src/NoneAlgo.cpp' \
)

names=(\
  "alternate" \
  "few      " \
  "some     " \
  "many     " \
  "none     " \
)

if [[ $1 == 'compile' ]]; then
  if [[ -d './build' ]]; then
    rm ./build/*
  fi
fi

for i in {0..4}; do
  algoname=${names[$i]}
  algofile=${algos[$i]}

  for datafile in $(ls ./data); do
    if ! [[ $datafile == 'README.md' ]]; then
      run $algoname $algofile $datafile
    fi
  done
done
