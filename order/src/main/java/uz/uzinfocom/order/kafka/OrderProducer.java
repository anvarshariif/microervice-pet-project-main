package uz.uzinfocom.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.uzinfocom.order.dto.OrdersResponseDto;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private static final String TOPIC = "orders";

  public void sendOrder(OrdersResponseDto order) {
    kafkaTemplate.send(TOPIC, order);
    log.info("✅ Order sent to Kafka: {}",order.toString());
  }
}
