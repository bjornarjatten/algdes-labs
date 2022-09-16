
import sys


file1 = sys.argv[1]
file2 = sys.argv[2]

lines1, lines2 = None, None

with open(file1, 'r') as f1:
  lines1 = f1.readlines()

with open(file2, 'r') as f2:
  lines2 = f2.readlines()


lines1.sort()
lines2.sort()

for i in range (len(lines1)):
    if lines1[i] != lines2[i]:
        raise 'Uh Oh'

