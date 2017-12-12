
### Command samples
#### Download Kafka and Zookeeper
    curl http://mirror.linux-ia64.org/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz | tar xvz
    curl http://mirror.linux-ia64.org/apache/zookeeper/zookeeper-3.3.6/zookeeper-3.3.6.tar.gz | tar xvz

#### start Zookeeper:
    zookeeper-server-start.sh $KAFKA_HOME/bin/kafka_2.11-1.0.0/config/zookeeper.properties

#### Start Kafka
    kafka-server-start.sh $KAFKA_HOME/config/server.properties

#### Start Kafka Consumer
    kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic messagesTopic --from-beginning
#### Start Kafka Producer
    kafka-console-producer.sh --broker-list localhost:9092 --topic test
