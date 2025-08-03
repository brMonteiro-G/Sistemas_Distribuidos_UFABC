FROM openjdk:17-jdk-slim

# Set ZooKeeper version
ENV ZK_VERSION=3.9.3
ENV ZK_HOME=/opt/zookeeper

# Install dependencies and download ZooKeeper
RUN apt-get update && apt-get install -y curl tar bash \
    && curl -sSL https://dlcdn.apache.org/zookeeper/zookeeper-${ZK_VERSION}/apache-zookeeper-${ZK_VERSION}-bin.tar.gz \
    | tar -xz -C /opt \
    && mv /opt/apache-zookeeper-${ZK_VERSION}-bin ${ZK_HOME} \
    && rm -rf /var/lib/apt/lists/*

# Copy your Java code and scripts
WORKDIR /app
COPY Executor.java .
COPY lista.sh .
COPY logback.xml ${ZK_HOME}/conf/logback.xml

# Compile Java code
RUN javac -cp ".:${ZK_HOME}/lib/*" Executor.java

# Set CLASSPATH for ZooKeeper jars
ENV CP=.:${ZK_HOME}/lib/zookeeper-${ZK_VERSION}.jar:${ZK_HOME}/lib/zookeeper-jute-${ZK_VERSION}.jar:${ZK_HOME}/lib/slf4j-api-1.7.30.jar:${ZK_HOME}/lib/logback-core-1.2.13.jar:${ZK_HOME}/lib/logback-classic-1.2.13.jar:${ZK_HOME}/lib/netty-handler-4.1.113.Final.jar

# Run the program (adjust host/paths as needed)
CMD ["sh", "-c", "java -cp $CP -Dlogback.configurationFile=file:${ZK_HOME}/conf/logback.xml Executor 127.0.0.1 /mcta025/ex4 out.txt ./lista.sh"]
