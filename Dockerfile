FROM ubuntu:24.04 AS build

WORKDIR /build

RUN apt update
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt -qq -y install curl wget unzip zip

RUN curl -s "https://get.sdkman.io" | bash
RUN chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"
RUN sdk install java 21.0.3-graal

COPY . .

RUN --mount=type=cache,target=/root/.gradle,sharing=private ./gradlew nativeCompile

FROM alpine:3.20.0

WORKDIR /app

COPY --from=build /build/build/native/nativeCompile/cloudflareddns /app/cloudflareddns
RUN chmod +x /app/cloudflareddns

ENTRYPOINT ["/app/cloudflareddns"]
CMD ["sync", "--keep-syncing"]
