package uz.uzinfocom.order.kafka;


import org.springframework.stereotype.Component;

import java.util.Map;
import uz.uzinfocom.order.dto.OrdersResponseDto;

@Component
public class CommonKafkaProducerTypeMappingProvider {

    private static final String ORDER_MESSAGE = "OrderMessage";

    public Map<String, String> getTypeMappings() {
        return Map.of(
               ORDER_MESSAGE, OrdersResponseDto.class.getName()
        );
    }

}

