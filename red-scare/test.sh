#!/bin/bash

for file in $(ls data); do
  if ! [[ $file == README.md ]]; then
    python3 src/RedScare.py $file $1 < ./data/$file
  fi
done
