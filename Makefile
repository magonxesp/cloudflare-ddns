.PHONY: first-version bump-version build

first-version:
	@python scripts/bump-version.py --first-version

bump-version:
	@python scripts/bump-version.py

build:
	./gradlew build
