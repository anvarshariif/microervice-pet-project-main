package uz.uzinfocom.notification.kafka;

import org.springframework.stereotype.Component;
import java.util.Map;
import uz.uzinfocom.notification.dto.OrdersDto;

@Component
public class KafkaTypeMapping {

  private static final String ORDER_MESSAGE = "OrderMessage";

  public Map<String, String> getTypeMappings() {
    return Map.of(
        ORDER_MESSAGE, OrdersDto.class.getName()
    );
  }

}
