package uz.uzinfocom.order;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.uzinfocom.order.dto.OrdersRequestDto;
import uz.uzinfocom.order.dto.OrdersResponseDto;
import uz.uzinfocom.order.extrenal.ProductClient;
import uz.uzinfocom.order.extrenal.dto.ProductResponseDto;
import uz.uzinfocom.order.kafka.OrderProducer;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

  private final OrdersRepository ordersRepository;
  private final ProductClient productClient;
  private final OrderProducer orderProducer;

  @Override
  @Transactional
  public OrdersResponseDto createOrder(OrdersRequestDto requestDTO) {
    log.info("Yangi buyurtma yaratish: {}", requestDTO);
    try {
      ResponseEntity<ProductResponseDto> productById = productClient.getProductById(
          requestDTO.getProductId()
      );
      log.info("product servisedan kelgan productId: {}", productById);
    }catch (Exception e) {
      log.warn("product service dan product olib kelishda xatolik: {}", e.getMessage());
      throw new RuntimeException("product service dan product olib kelishda xatolik");
    }
    OrdersEntity order = toEntity(requestDTO);
    OrdersEntity savedOrder = ordersRepository.save(order);
    log.info("Buyurtma yaratildi. ID: {}", savedOrder.getId());

    OrdersResponseDto responseDto = toDTO(savedOrder);
    orderProducer.sendOrder(responseDto);
    return responseDto;
  }

  @Override
  @Transactional
  public Page<OrdersResponseDto> getAllOrders(int page, int size, String sortBy, String direction) {
    log.info("Barcha buyurtmalarni olish. Page: {}, Size: {}", page, size);

    Sort sort = direction.equalsIgnoreCase("ASC")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    return ordersRepository.findAll(pageable)
        .map(this::toDTO);
  }

  @Override
  @Transactional
  public Page<OrdersResponseDto> getOrdersByUserId(Long userId, int page, int size) {
    log.info("User buyurtmalarini topish. UserID: {}", userId);

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    return ordersRepository.findByUserId(userId, pageable)
        .map(this::toDTO);
  }

  public OrdersEntity toEntity(OrdersRequestDto dto) {
    OrdersEntity order = new OrdersEntity();
    order.setUserId(dto.getUserId());
    order.setProductId(dto.getProductId());
    order.setQuantity(dto.getQuantity());
    order.setTotalPrice(dto.getTotalPrice());
    order.setCreatedAt(LocalDateTime.now());
    order.setUpdatedAt(LocalDateTime.now());
    return order;
  }

  public OrdersResponseDto toDTO(OrdersEntity order) {
    return new OrdersResponseDto(
        order.getId(),
        order.getUserId(),
        order.getProductId(),
        order.getQuantity(),
        order.getTotalPrice(),
        order.getCreatedAt(),
        order.getUpdatedAt()
    );
  }


}
