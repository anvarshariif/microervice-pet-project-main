package uz.uzinfocom.notification.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import java.util.HashMap;
import java.util.Map;
import uz.uzinfocom.notification.dto.OrdersDto;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  private final KafkaTypeMapping typeMappingProvider;

  public KafkaConsumerConfig(KafkaTypeMapping typeMappingProvider) {
    this.typeMappingProvider = typeMappingProvider;
  }

  @Bean
  public ConsumerFactory<String, OrdersDto> orderConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    // Xatoliklardan himoyalangan deserializer
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    var typeMappings = new StringBuilder();
    typeMappingProvider.getTypeMappings().forEach((key, value) ->
        typeMappings.append(key).append(":").append(value).append(","));

    if (!typeMappings.isEmpty()) {
      typeMappings.deleteCharAt(typeMappings.length() - 1);
      props.put(JsonDeserializer.TYPE_MAPPINGS, typeMappings.toString());
    }

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, OrdersDto> orderKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, OrdersDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(orderConsumerFactory());
    factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(2000L, 3)));
    return factory;
  }
}
