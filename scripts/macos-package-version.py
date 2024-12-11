import argparse
import re

parser = argparse.ArgumentParser(prog='Replace version for macOS package')
parser.add_argument('-v', '--version', dest='version', default=False)
args = parser.parse_args()

version = str(args.version)
regex = r'^(v?)([0-9]+)'
match = re.match(regex, version)
prefix = match.group(1)
number = int(match.group(2))

adapted = re.sub(regex, prefix + str(number + 1), version)
print(adapted)
