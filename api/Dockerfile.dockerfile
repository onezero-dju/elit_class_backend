FROM openjdk:17-jdk-slim


RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    apt-utils \
    ca-certificates \
    curl \
    unzip \
    bash \
    xz-utils \
    findutils \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src

EXPOSE 8080

CMD ["sh", "-c", "exec tail -f /dev/null"]