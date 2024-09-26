FROM eclipse-temurin:21-jdk AS build

WORKDIR /builder

COPY . .

RUN --mount=type=cache,target=/root/.gradle,sharing=private ./gradlew build

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /builder/build/libs/cloudflare-ddns.jar /app/cloudflare-ddns.jar

ENTRYPOINT ["java", "-Xmx25m", "-jar", "/app/cloudflare-ddns.jar"]
