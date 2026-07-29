.PHONY: deb deb-debug sandbox

deb:
	cargo build --locked --release
	bash scripts/deb-package.sh release

deb-debug:
	cargo build --locked
	bash scripts/deb-package.sh debug

sandbox:
	mkdir -p sandbox/home/.config/cloudflare-ddns
	test -f sandbox/home/.config/cloudflare-ddns/config.yaml \
		|| cp example-config.yaml sandbox/home/.config/cloudflare-ddns/config.yaml
	chmod 600 sandbox/home/.config/cloudflare-ddns/config.yaml
