FROM debian:bookworm

WORKDIR /builder

RUN apt update \
    && apt install -y wget make fakeroot build-essential

RUN wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%2B11/OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz \
    && tar -zxvf OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz \
    && rm OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz

ENV JAVA_HOME=/builder/jdk-21.0.5+11/bin
ENV PATH=/builder/jdk-21.0.5+11/bin:$PATH

COPY build build
COPY package package
COPY scripts scripts
COPY Makefile Makefile
COPY LICENSE LICENSE

RUN rm -rf build/tmp/jpackage/*

CMD ["make", "release-debian"]
