.PHONY: first-version bump-version build

first-version:
	@deno run --allow-run --allow-write --allow-read scripts/bump-version.js --first-version

bump-version:
	@deno run --allow-run --allow-write --allow-read scripts/bump-version.js

build:
	./gradlew nativeCompile
