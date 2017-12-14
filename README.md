# Command samples
This tutorial is based on 
- [https://kafka.apache.org/quickstart]
- [https://kafka.apache.org/10/documentation/streams/tutorial]
- [https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams]
- [https://dzone.com/articles/join-semantics-in-kafka-streams] 

## Download Kafka and Zookeeper
```bash
curl http://mirror.linux-ia64.org/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz | tar xvz
curl http://mirror.linux-ia64.org/apache/zookeeper/zookeeper-3.4.11/zookeeper-3.4.11.tar.gz | tar xvz
```
## Export variables
```bash
export KAFKA_HOME=`pwd`/kafka_2.11-1.0.0
```


## Start Zookeeper:
```bash
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
```

## Start Kafka
```bash
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
```

## Attach Kafka File System Connector
```bash
$KAFKA_HOME/bin/connect-standalone.sh $KAFKA_HOME/config/connect-standalone.properties config/login-source.properties config/click-source.properties config/login-sink.properties config/click-sink.properties

cp data/login.txt /tmp/
cp data/click.txt /tmp/

echo \{"user":"nsurname", "token":"126"} >> /tmp/login.txt
echo \{"token":"126", url:"projects.spring.io"} >> /tmp/click.txt
```
Check `*.sink.txt` files for changes


