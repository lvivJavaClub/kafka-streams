package myapps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JCCWorkshop {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        Serde<String> stringSerde = Serdes.serdeFrom(String.class);
        Serde<Long> longSerde = Serdes.serdeFrom(Long.class);
        Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(
                new WindowedSerializer<String>(stringSerde.serializer()), 
                new WindowedDeserializer<String>(stringSerde.deserializer())
        );
        
        Serde<JsonNode> jsonSerde = Serdes.serdeFrom(
                new JsonSerializer(), 
                new JsonDeserializer()
        );

        TimestampExtractor tsExtractor = new JCCTimeStampExtractor(); 
        
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> loginStream = builder.stream(
                "login-topic",
                Consumed.with(
                        stringSerde, jsonSerde,
                        tsExtractor, Topology.AutoOffsetReset.EARLIEST
                )
        ).selectKey(
                (key, value) -> value.path("user").textValue()
        );
        
        KStream<String, JsonNode> clickStream = builder.stream(
                "click-topic",
                Consumed.with(
                        stringSerde, jsonSerde,
                        tsExtractor, Topology.AutoOffsetReset.EARLIEST
                )
        ).selectKey(
                (key, value) -> value.path("token").textValue()
        );

        KStream<String, JsonNode> joinedStream = loginStream
                .selectKey(
                        (key, value) -> value.path("token").textValue()
                )
                .join(
                        clickStream,
                        (login, click) -> {
                            ObjectNode enricherNode = mapper.createObjectNode();
                            enricherNode.set("user", login.get("user"));
                            return enricherNode.setAll((ObjectNode) click);
                        },
                        JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
                        Joined.with(stringSerde, jsonSerde, jsonSerde)
                );

        joinedStream.print(Printed.toSysOut());

        joinedStream
                .to("joined-topic", Produced.with(stringSerde, jsonSerde));

        KStream<Windowed<String>, String> userClickCountStream = builder.stream(
                "joined-topic",
                Consumed.with(
                        stringSerde, jsonSerde,
                        tsExtractor, Topology.AutoOffsetReset.EARLIEST
                )
        ).groupBy((token, userClick) ->
                        userClick.path("user").textValue(),
                Serialized.with(stringSerde, jsonSerde)
        )
                .windowedBy(SessionWindows.with(50))
                .count()
                .toStream()
                .map((key, count) -> KeyValue.pair(key, key.key() + ":" + String.valueOf(count)));
        
        userClickCountStream.print(Printed.toSysOut());
        
        userClickCountStream
                .to("user-click-count-topic",
                        Produced.with(windowedSerde , stringSerde));
        
        final Topology topology = builder.build();

        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}
