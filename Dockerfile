FROM eclipse-temurin:21-jdk AS build

WORKDIR /builder

COPY . .

RUN --mount=type=cache,target=/root/.gradle,sharing=private ./gradlew build

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /builder/build/libs/cloudflare-ddns-*-all.jar /app/cloudflare-ddns.jar

ENTRYPOINT ["java", "-jar", "/app/cloudflare-ddns.jar"]
