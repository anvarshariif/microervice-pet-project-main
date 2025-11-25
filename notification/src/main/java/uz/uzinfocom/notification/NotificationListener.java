package uz.uzinfocom.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uz.uzinfocom.notification.dto.OrdersDto;

@Slf4j
@Service
public class NotificationListener {

  private final NotificationRepository notificationRepository;

  public NotificationListener(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @KafkaListener(topics = "orders", groupId = "notification-group", containerFactory = "orderKafkaListenerContainerFactory")
  public void consumeOrder(OrdersDto orderDTO) {
    notificationRepository.save(
        new NotificationEntity(orderDTO.getUserId(), orderDTO.getProductId())
    );
    log.info("notification servici dto qabul qildi: {}", orderDTO.toString());
  }
}
