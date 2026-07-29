# Cloudflare DDNS

A small synchronous client that updates Cloudflare DNS records with the current
public IP address. It supports IPv4 (`A`) and IPv6 (`AAAA`) records.

The program performs one update pass and exits. Use cron, a systemd timer or
another scheduler to choose how often it runs.

## Install

On Debian-based Linux distributions (`amd64` and `arm64`):

```sh
curl -L https://raw.githubusercontent.com/magonxesp/cloudflare-ddns/refs/heads/main/scripts/install.sh |
  sudo bash -
```

The installer downloads the package for the current architecture from the
[latest GitHub release](https://github.com/magonxesp/cloudflare-ddns/releases/latest)
and installs it with `dpkg`.

## Configuration

Copy the example configuration:

```sh
mkdir -p ~/.config/cloudflare-ddns
curl -L \
  https://raw.githubusercontent.com/magonxesp/cloudflare-ddns/refs/heads/main/example-config.yaml \
  -o ~/.config/cloudflare-ddns/config.yaml
```

The configuration is discovered in this order:

1. `--config <FILE>`
2. `~/.cloudflare-ddns/config.yaml`
3. `$XDG_CONFIG_HOME/cloudflare-ddns/config.yaml`, or
   `~/.config/cloudflare-ddns/config.yaml`
4. `/etc/cloudflare-ddns/config.yaml`

Set the API token in `config.yaml`:

```yaml
cloudflare:
  api_token: your-cloudflare-api-token
```

The token needs DNS edit permission for the configured zones. A complete
annotated configuration is available in
[`example-config.yaml`](example-config.yaml).

Because the configuration contains a credential, restrict its permissions:

```sh
chmod 600 ~/.config/cloudflare-ddns/config.yaml
```

## Usage

Run one update pass:

```sh
cloudflare-ddns
```

Inspect changes without writing to Cloudflare:

```sh
cloudflare-ddns --dry-run
```

Use a specific configuration:

```sh
cloudflare-ddns --config /path/to/config.yaml
```

## Scheduled execution

For example, this cron entry runs the client every five minutes:

```cron
*/5 * * * * /usr/bin/cloudflare-ddns
```

## Docker

```sh
docker pull magonx/cloudflare-ddns:latest
docker run --rm \
  -v "$PWD/config.yaml:/etc/cloudflare-ddns/config.yaml:ro" \
  magonx/cloudflare-ddns:latest
```

## Build and test

Rust 1.85 or newer is required because the project uses the Rust 2024 edition.

```sh
cargo build --release
cargo test
```

The release executable is written to `target/release/cloudflare-ddns`.

## Debian package

On a Debian-based system with `dpkg-deb` installed:

```sh
make deb
```

The package is written to:

```text
target/release/bundle/cloudflare-ddns_<version>_<architecture>.deb
```

Install it with:

```sh
sudo apt install ./target/release/bundle/cloudflare-ddns_*.deb
```

The executable is installed in `/usr/bin/cloudflare-ddns`. An annotated
configuration is installed in `/usr/share/cloudflare-ddns/config.yaml` as a
reference; copy it to `/etc/cloudflare-ddns/config.yaml` before editing it.

## Testing from non-Linux systems

The services in `docker-compose.yml` allow the Linux build, Debian package and
application container to be tested from macOS or another system with Docker
Compose installed.

Prepare the home directory shared by the containers:

```sh
make sandbox
```

This creates `sandbox/home/.config/cloudflare-ddns/config.yaml` from the example
configuration. Edit that copy with the zones and records used for testing.

Build the Debian package with the `packager` service:

```sh
docker compose run --rm packager
```

The resulting package is written to `target/release/bundle`, just like a local
`make deb` invocation. Its architecture matches the container architecture.

Install the package in a disposable Debian container and verify the executable:

```sh
docker compose run --rm sandbox bash -lc \
  'dpkg --install target/release/bundle/cloudflare-ddns_*.deb && cloudflare-ddns --help'
```

To test the application image itself, put the token in the sandbox
`config.yaml` and perform a dry run:

```sh
docker compose run --rm \
  cloudflare_ddns \
  --dry-run
```
