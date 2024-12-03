.PHONY: first-version bump-version build release-linux

first-version:
	@python scripts/bump-version.py --first-version

bump-version:
	@python scripts/bump-version.py

build:
	./gradlew build

release-debian:
	jpackage \
		--name cloudflare-ddns \
		--about-url 'https://github.com/magonxesp/cloudflare-ddns' \
		--app-version $$(cat build/libs/version.txt) \
		--input build/libs \
		--main-jar $$(find build/libs -name "*-all.jar" | sed 's/build\/libs\///g') \
		--dest release \
		--type deb \
		--resource-dir package/deb \
		--linux-deb-maintainer 'magonxesp@gmail.com' \
		--linux-shortcut
