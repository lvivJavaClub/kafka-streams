package myapps;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper;

    private Class<T> tClass;

    public JsonDeserializer() {
        objectMapper = new ObjectMapper();
    }

    public JsonDeserializer(Class<T> tClass) {
        this();

        this.tClass = tClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> configs, boolean isKey) {
        tClass = (Class<T>) configs.get("JSONType");
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        T item;
        try {
            item = objectMapper.readValue(data, tClass);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return item;
    }

    @Override
    public void close() {

    }
}
