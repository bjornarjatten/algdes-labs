#!/bin/sh
for FILE in *-in.txt

do
  echo '------------------------------------------------'
  echo ' '
	echo $FILE
	base=${FILE%-in.txt}
    java ../solution/StableMatching.java < $FILE > $base.yourname.out.txt
    python3 matchCompare.py $base.yourname.out.txt $base-out.txt
  echo ' '
  echo ' '
  echo ' '
  echo ' '
done
