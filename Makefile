.PHONY: bump-version bump-version-alpha install-cli-app build-backend-image expose-backend

bump-version:
	@deno run --allow-run --allow-write --allow-read scripts/bump-version.js

bump-version-alpha:
	@deno run --allow-run --allow-write --allow-read scripts/bump-version.js --alpha

bump-version-beta:
	@deno run --allow-run --allow-write --allow-read scripts/bump-version.js --beta
