.PHONY: first-version bump-version build publish-release

first-version:
	@python3 scripts/bump-version.py --first-version

bump-version:
	@python3 scripts/bump-version.py

build:
	./gradlew build

publish-release:
	./scripts/create-github-release.sh
