FROM rust:1.97-alpine AS builder

WORKDIR /build

RUN apk add --no-cache musl-dev

COPY Cargo.toml Cargo.lock ./
COPY src ./src

RUN cargo build --locked --release

FROM alpine:3

WORKDIR /

RUN apk add --no-cache ca-certificates
COPY --from=builder /build/target/release/cloudflare-ddns /usr/local/bin/cloudflare-ddns

ENTRYPOINT ["/usr/local/bin/cloudflare-ddns"]
