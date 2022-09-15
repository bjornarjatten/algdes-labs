#!/bin/bash

for FILE in ../data/*-tsp.txt

do
  echo $FILE
  javac ./ClosestPoints.java
  java ClosestPoints < $FILE

done