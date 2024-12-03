.PHONY: first-version bump-version build release-linux

first-version:
	@python scripts/bump-version.py --first-version

bump-version:
	@python scripts/bump-version.py

build:
	./gradlew build

release-debian:
	./scripts/deb-package.sh
