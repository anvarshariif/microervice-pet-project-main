package uz.uzinfocom.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzinfocom.order.dto.OrdersRequestDto;
import uz.uzinfocom.order.dto.OrdersResponseDto;
import uz.uzinfocom.order.kafka.OrderProducer;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

  private final OrdersService orderService;


  @PostMapping
  public ResponseEntity<OrdersResponseDto> createOrder(@RequestBody OrdersRequestDto requestDTO) {
    log.info("Yangi buyurtma yaratish");
    OrdersResponseDto response = orderService.createOrder(requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<Page<OrdersResponseDto>> getAllOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction) {
    log.info("GET /api/orders - Barcha buyurtmalarni olish");
    Page<OrdersResponseDto> response = orderService.getAllOrders(page, size, sortBy, direction);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<Page<OrdersResponseDto>> getOrdersByUserId(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    log.info("GET /api/orders/user/{} - User buyurtmalarini olish", userId);
    Page<OrdersResponseDto> response = orderService.getOrdersByUserId(userId, page, size);
    return ResponseEntity.ok(response);
  }
}
