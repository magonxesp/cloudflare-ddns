FROM golang:1.24-alpine AS builder

WORKDIR /build

COPY . .

RUN go build

FROM golang:1.24-alpine

LABEL org.opencontainers.image.title="Cloudflare DDNS"
LABEL org.opencontainers.image.authors="MagonxESP"
LABEL org.opencontainers.image.licenses="MIT"
LABEL org.opencontainers.image.description="A Dynamic DNS client that automatically updates your IP in Cloudflare DNS records, ensuring your domain stays accessible without the need for a static IP."
LABEL org.opencontainers.image.url="https://github.com/magonxesp/cloudflare-ddns"
LABEL org.opencontainers.image.source="https://github.com/magonxesp/cloudflare-ddns"

WORKDIR /

COPY --from=build /build/cloudflare-ddns /opt/ccloudflare-ddns
RUN chmod +x /opt/ccloudflare-ddns

ENTRYPOINT ["/opt/ccloudflare-ddns"]
