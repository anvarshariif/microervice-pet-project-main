package uz.uzinfocom.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    private final CommonKafkaProducerTypeMappingProvider typeMappingProvider;

    public KafkaProducerConfig(CommonKafkaProducerTypeMappingProvider typeMappingProvider) {
        this.typeMappingProvider = typeMappingProvider;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);



        var typeMappings = new StringBuilder();
        typeMappingProvider.getTypeMappings().forEach((key, value) ->
                typeMappings.append(key).append(":").append(value).append(","));

        if (!typeMappings.isEmpty()) {
            typeMappings.deleteCharAt(typeMappings.length() - 1);
            config.put(JsonSerializer.TYPE_MAPPINGS, typeMappings.toString());
        }

        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ObjectMapper objectMapper) {
        return new KafkaTemplate<>(producerFactory(objectMapper));
    }




}
