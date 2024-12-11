.PHONY: first-version bump-version build release-linux publish-release

first-version:
	@python scripts/bump-version.py --first-version

bump-version:
	@python scripts/bump-version.py

build:
	./gradlew build

release-debian:
	./scripts/make-package.sh debian

release-macos:
	./scripts/make-package.sh macos

publish-release:
	./scripts/create-github-release.sh
