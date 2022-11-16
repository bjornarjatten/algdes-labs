
import sys

for line in sys.stdin:
    file, n, alt, few, many, none, some = line.split()
    if int(n) >= 500:
        print(file, end='&\t')
        print(n, end=' &\t')
        print(alt, end=' &\t')
        print(few, end=' &\t')
        print(many, end=' &\t')
        print(none, end=' &\t')
        print(some, end=' &\t')
        print('\\\\')

