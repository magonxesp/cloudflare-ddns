FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /build

COPY . .

RUN apt update && apt install -y curl libcurl4-gnutls-dev
RUN --mount=type=cache,target=/root/.gradle,sharing=private ./gradlew linuxX64Binaries

FROM ubuntu:noble
# Ubuntu 24.04.1

WORKDIR /app

COPY --from=build /build/build/bin/linuxX64/releaseExecutable/cloudflare-ddns.kexe /app/cloudflare-ddns
RUN apt update && apt install -y curl libcurl4-gnutls-dev
RUN chmod +x /app/cloudflare-ddns

ENTRYPOINT ["/app/cloudflare-ddns"]
