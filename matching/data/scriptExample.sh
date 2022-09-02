#!/bin/sh
for FILE in *-in.txt

do
	echo $FILE
	base=${FILE%-in.txt}
    java ../solution/StableMatching.java < $FILE > $base.yourname.out.txt
    diff $base.yourname.out.txt $base-out.txt

    if [[ ?$ ]]; then 

      python3 matchCompare.py $base.yourname.out.txt $base-out.txt > /dev/null
      if [[ ?$ ]]; then
        echo "different order of pairs, however matching is identical"
      else
        continue
      fi 

    fi

done
