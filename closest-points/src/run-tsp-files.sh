#!/bin/bash

function print() {
  for FILE in ../data/*-tsp.txt
  do
    if [[ $FILE =~ (.*)\-(.*) ]]
    then
      javac ./ClosestPoints.java
      echo -n "${BASH_REMATCH[1]}.tsp: "
      java ClosestPoints < $FILE
    else
      echo "Could not figure out format"
    fi  
  done
}

print > output.txt
diff output.txt ../data/closest-pair-out.txt > diffOutput.txt
