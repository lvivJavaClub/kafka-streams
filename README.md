# Installation
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

# Run
## Attach Kafka File System Connector
```bash
$KAFKA_HOME/bin/connect-standalone.sh config/connect-standalone.properties config/login-source.properties config/click-source.properties config/joined-sink.properties config/user-clicks-sink.properties

cp data/login.txt /tmp/
cp data/click.txt /tmp/

echo \{\"user\":\"nsurname\", \"token\":\"126\", \"ts\":100} >> /tmp/login.txt
echo \{\"token\":\"126\", \"url\":\"projects.spring.io\", \"ts\":120} >> /tmp/click.txt

$KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic login-topic --from-beginning
```
Check `*.sink.txt` files for changes

# Reading list
- [https://kafka.apache.org/quickstart]
- [https://docs.confluent.io/3.0.0/streams/architecture.html]
- [https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams]
- [https://docs.confluent.io/current/streams/concepts.html] 
- [http://vishnuviswanath.com/kafka-streams-part2.html]
- [https://kafka.apache.org/10/documentation/streams/tutorial]
- [https://www.confluent.io/blog/data-reprocessing-with-kafka-streams-resetting-a-streams-application]
- [https://kafka.apache.org/10/documentation/streams/developer-guide#streams_dsl_transform]

