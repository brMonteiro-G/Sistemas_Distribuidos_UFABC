export ZK=~/mcta025/zookeeper-3.9.3
echo "ZK=$ZK"
export CP=.:$ZK'/lib/zookeeper-3.9.3.jar':$ZK'/lib/zookeeper-jute-3.9.3.jar':$ZK'/lib/slf4j-api-1.7.30.jar':$ZK'/lib/logback-core-1.2.13.jar':$ZK'/lib/logback-classic-1.2.13.jar':$ZK'/lib/netty-handler-4.1.113.Final.jar'
echo "CP=$CP_ZK"
javac -cp $CP_ZK *.java
echo "***** Lock test"
export WAIT=10000
echo "Wait time = $WAIT"
java -cp $CP_ZK -Dlogback.configurationFile=file:$ZK/conf/logback.xml SyncPrimitive lock localhost $WAIT
