FROM openjdk:17-jdk-slim

# Set ZooKeeper version
ENV ZK_VERSION=3.9.3
ENV ZK_HOME=/opt/zookeeper

# Install dependencies, Maven, and download ZooKeeper
RUN apt-get update && apt-get install -y \
    curl \
    tar \
    bash \
    maven \
    netcat \
 && curl -sSL https://dlcdn.apache.org/zookeeper/zookeeper-${ZK_VERSION}/apache-zookeeper-${ZK_VERSION}-bin.tar.gz \
    | tar -xz -C /opt \
 && mv /opt/apache-zookeeper-${ZK_VERSION}-bin ${ZK_HOME} \
 && rm -rf /var/lib/apt/lists/*

RUN ln -sf /bin/bash /bin/sh

RUN mv ${ZK_HOME}/conf/zoo_sample.cfg ${ZK_HOME}/conf/zoo.cfg
RUN echo "server.1=172.28.0.11:2888:3888 server.2=172.28.0.12:2888:3888 server.3=172.28.0.13:2888:3888" > zoo.cfg

# Set working directory
WORKDIR /app

# Copy source code and config
COPY . .
COPY logback.xml ${ZK_HOME}/conf/logback.xml

RUN chmod 777 ./build_and_run.sh
#RUN mv debug-zk.sh /opt/zookeeper/bin

# Set CLASSPATH for ZooKeeper jars
ENV CP=.:${ZK_HOME}/lib/zookeeper-${ZK_VERSION}.jar:${ZK_HOME}/lib/zookeeper-jute-${ZK_VERSION}.jar:${ZK_HOME}/lib/slf4j-api-1.7.30.jar:${ZK_HOME}/lib/logback-core-1.2.13.jar:${ZK_HOME}/lib/logback-classic-1.2.13.jar:${ZK_HOME}/lib/netty-handler-4.1.113.Final.jar

CMD [ "sh", "-c", "sleep infinity"]
# Default command (infinite sleep for debugging)
#ENTRYPOINT ["sh", "build_and_run.sh"]